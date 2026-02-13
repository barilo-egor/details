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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DetailsService extends BasePersistService<Details> implements IDetailsService {

    private final DetailsRepository detailsRepository;

    private final DetailsMapper mapper;

    public DetailsService(DetailsRepository detailsRepository, DetailsMapper mapper) {
        this.detailsRepository = detailsRepository;
        this.mapper = mapper;
    }

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
    public Details getTarget(List<Long> detailIds, Integer amount) {
        List<Long> sortedDetails = detailIds.stream().distinct().sorted().toList();
        List<Details> targetDetails = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(sortedDetails);
        if (!targetDetails.isEmpty()) {
            for (Details details : targetDetails) {
                int targetAmount = details.getTargetAmount();
                int receiveAmount = Objects.nonNull(details.getReceivedAmount()) ? details.getReceivedAmount() : 0;
                int reserveAmount = Objects.nonNull(details.getReserveAmount()) ? details.getReserveAmount() : 0;
                if (targetAmount - receiveAmount - reserveAmount >= amount && details.isInRange(amount)) {
                    details.setReserveAmount(reserveAmount + amount);
                    return detailsRepository.save(details);
                }
            }
        }

        throw new EntityNotFoundException("Не найден целевой реквизит");
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
    public Details findById(Long pid) {
        Optional<Details> maybeDetails = detailsRepository.findById(pid);
        if (maybeDetails.isEmpty()) {
            throw new EntityNotFoundException("Не найден реквизит " + pid);
        }
        return maybeDetails.get();
    }

    @Override
    protected BaseRepository<Details> getBaseRepository() {
        return detailsRepository;
    }

    @Override
    @Transactional
    public void saveReserveAmount(Long detailsId, Integer dealAmount) {
        Details details = findById(detailsId);
        int reserveAmount = Objects.isNull(details.getReserveAmount()) ? 0 : details.getReserveAmount();
        details.setReserveAmount(reserveAmount - dealAmount);
        detailsRepository.save(details);
    }

    @Override
    @Transactional
    public Details confirmPayment(Long detailsId, Integer dealAmount) {
        Details details = findById(detailsId);
        int reserveAmount = Objects.isNull(details.getReserveAmount()) ? 0 : details.getReserveAmount();
        int receivedAmount = Objects.isNull(details.getReceivedAmount()) ? 0 : details.getReceivedAmount();
        details.setReserveAmount(reserveAmount - dealAmount);
        details.setReceivedAmount(receivedAmount + dealAmount);
        return detailsRepository.save(details);
    }

    @Override
    public List<Details> saveAll(List<Details> detailsDto) {
        return detailsRepository.saveAll(detailsDto);
    }

    @Transactional
    public void patchDetails(Long pid, DetailsDto dto, boolean updateNotNull) {
        Details details = findById(pid);
        if (updateNotNull) {
            mapper.updateEntityFromDtoNotNull(dto, details);
        } else {
            mapper.updateEntityFromDto(dto, details);
        }
        detailsRepository.save(details);
    }

}
