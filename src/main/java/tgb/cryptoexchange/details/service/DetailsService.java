package tgb.cryptoexchange.details.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.dto.DetailsMapper;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;
import tgb.cryptoexchange.details.interfaces.service.IDetailsService;
import tgb.cryptoexchange.details.repository.BaseRepository;
import tgb.cryptoexchange.details.repository.DetailsRepository;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class DetailsService extends BasePersistService<Details> implements IDetailsService {

    private final Map<Long, Integer> PAYMENT_REQUISITE_ORDER = new HashMap<>();

    private final DetailsRepository detailsRepository;

    private final DetailsMapper mapper;

    public DetailsService(DetailsRepository detailsRepository, DetailsMapper mapper) {
        this.detailsRepository = detailsRepository;
        this.mapper = mapper;
    }

    //    public Integer getOrder(Long paymentTypePid) {
    //        synchronized (this) {
    //            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentTypePid);
    //            if (Objects.isNull(order)) {
    //                order = 0;
    //                PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order);
    //            }
    //            return order;
    //        }
    //    }

    //    @Override
    //    public void checkOrder(PaymentTypeDto paymentType) {
    //        synchronized (this) {
    //            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentType.getPid());
    //            if (Objects.isNull(order)) {
    //                order = 0;
    //                PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order);
    //            } else {
    //                List<Details> details = findAllByPids(paymentType.getDetails());
    //                long paymentTypeRequisitesSize = details.stream().filter(d -> Boolean.TRUE.equals(d.getIsOn())).count();
    //                if (order >= paymentTypeRequisitesSize) {
    //                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), 0);
    //                }
    //            }
    //        }
    //    }

    //    @Override
    //    public void updateOrder(PaymentTypeDto paymentType) {
    //        synchronized (this) {
    //            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentType.getPid());
    //            if (Objects.isNull(order)) {
    //                order = 0;
    //                PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order);
    //            } else {
    //                List<Details> details = findAllByPids(paymentType.getDetails());
    //                long paymentTypeRequisitesSize = details.stream().filter(d -> Boolean.TRUE.equals(d.getIsOn())).count();
    //                if (order + 1 >= paymentTypeRequisitesSize)
    //                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), 0);
    //                else
    //                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order + 1);
    //            }
    //        }
    //    }

    //    public void removeOrder(Long paymentTypePid) {
    //        synchronized (this) {
    //            PAYMENT_REQUISITE_ORDER.remove(paymentTypePid);
    //        }
    //    }

    @Override
    @Transactional
    public String getNotTargetRequisite(PaymentTypeDto paymentType) {
        Optional<Details> detailsOptional = detailsRepository.findOldestAvailableDetail(paymentType.getDetails());
        if (detailsOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Не найден ни один подходящий реквизит для " + paymentType.getName() + ".");
        }
        Details details = detailsOptional.get();
        details.setLastAccessedAt(Instant.now());
        return details.getRequisite();
    }

    @Override
    @Transactional
    public Optional<Details> getTarget(List<Long> detailIds, Integer amount) {
        List<Long> sortedDetails = detailIds.stream().distinct().sorted().toList();
        List<Details> targetDetails = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(sortedDetails);
        if (!targetDetails.isEmpty()) {
            for (Details details : targetDetails) {
                int targetAmount = details.getTargetAmount();
                int receiveAmount = Objects.nonNull(details.getReceivedAmount()) ? details.getReceivedAmount() : 0;
                int reserveAmount = Objects.nonNull(details.getReserveAmount()) ? details.getReserveAmount() : 0;
                if (targetAmount - receiveAmount - reserveAmount >= amount && details.isInRange(amount)) {
                    details.setReserveAmount(reserveAmount + amount);
                    detailsRepository.save(details);
                    return Optional.of(details);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Details> findAllByPids(List<Long> detailIds) {
        return detailsRepository.findAllByPidIn(detailIds);
    }
    //
    //    @Override
    //    public List<Details> getByPaymentType(PaymentType paymentType) {
    //        return detailsRepository.getByPaymentType(paymentType);
    //    }
    //
    //
    //    @Override
    //    public List<Details> getByPaymentType_Pid(Long paymentTypePid) {
    //        return detailsRepository.getByPaymentType_Pid(paymentTypePid);
    //    }
    //
    //    @Override
    //    public PaymentType getPaymentTypeByPid(Long pid) {
    //        return detailsRepository.getPaymentTypeByPid(pid);
    //    }
    //
    //    @Override
    //    public Integer countByPaymentTypePidAndIsOn(Long paymentTypePid) {
    //        return detailsRepository.countByPaymentTypePidAndIsOn(paymentTypePid);
    //    }

    @Override
    public void updateRequisiteByPid(String requisite, Long pid) {
        detailsRepository.updateRequisiteByPid(requisite, pid);
    }

    @Override
    public List<Details> getWithNotEmptyTargetAmount() {
        return detailsRepository.getWithNotEmptyTargetAmount();
    }

    @Override
    public Optional<Details> findByIdOptional(Long pid) {
        return detailsRepository.findById(pid);
    }

    @Override
    protected BaseRepository<Details> getBaseRepository() {
        return detailsRepository;
    }

    @Override
    @Transactional
    public void saveReserveAmount(Long detailsId, Integer dealAmount) {
        Optional<Details> maybeDetails = findByIdOptional(detailsId);
        if (maybeDetails.isPresent()) {
            Details details = maybeDetails.get();
            int reserveAmount = Objects.isNull(details.getReserveAmount()) ? 0 : details.getReserveAmount();
            details.setReserveAmount(reserveAmount - dealAmount);
            detailsRepository.save(details);
        }
    }

    @Override
    @Transactional
    public void confirmPayment(Long detailsId, Integer dealAmount) {
        Optional<Details> maybeDetails = findByIdOptional(detailsId);
        if (maybeDetails.isPresent()) {
            Details details = maybeDetails.get();
            int reserveAmount = Objects.isNull(details.getReserveAmount()) ? 0 : details.getReserveAmount();
            int receivedAmount = Objects.isNull(details.getReceivedAmount()) ? 0 : details.getReceivedAmount();
            details.setReserveAmount(reserveAmount - dealAmount);
            details.setReceivedAmount(receivedAmount + dealAmount);
            detailsRepository.save(details);
        }
    }

    @Override
    public List<Details> saveAll(List<Details> detailsDto) {
        return detailsRepository.saveAll(detailsDto);
    }

    @Transactional
    public void patchDetails(Long pid, DetailsDto dto) {
        Optional<Details> entity = detailsRepository.findById(pid);
        if (entity.isEmpty()) {
            throw new EntityNotFoundException("Не найден details " + pid + ".");
        }
        Details details = entity.get();
        mapper.updateEntityFromDto(dto, details);
        detailsRepository.save(details);
    }

}
