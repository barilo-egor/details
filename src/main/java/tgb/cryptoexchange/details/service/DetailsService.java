package tgb.cryptoexchange.details.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.exception.BaseException;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;
import tgb.cryptoexchange.details.interfaces.service.IDetailsService;
import tgb.cryptoexchange.details.repository.BaseRepository;
import tgb.cryptoexchange.details.repository.DetailsRepository;

import java.util.*;

@Service
@Slf4j
public class DetailsService extends BasePersistService<Details> implements IDetailsService {

    public static final Object TARGET_PAYMENT_REQUISITE_SYNCHRONIZE_OBJECT = new Object();

    private final DetailsRepository detailsRepository;

    private final Map<Long, Integer> PAYMENT_REQUISITE_ORDER = new HashMap<>();

    public DetailsService(DetailsRepository detailsRepository) {
        this.detailsRepository = detailsRepository;
    }

    public Integer getOrder(Long paymentTypePid) {
        synchronized (this) {
            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentTypePid);
            if (Objects.isNull(order)) {
                order = 0;
                PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order);
            }
            return order;
        }
    }

    @Override
    public void checkOrder(PaymentTypeDto paymentType) {
        synchronized (this) {
            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentType.getPid());
            if (Objects.isNull(order)) {
                order = 0;
                PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order);
            } else {
                List<Details> details = findAllByPids(paymentType.getDetails());
                long paymentTypeRequisitesSize = details.stream().filter(d->Boolean.TRUE.equals(d.getIsOn())).count();
                if (order >= paymentTypeRequisitesSize) {
                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), 0);
                }
            }
        }
    }

    @Override
    public void updateOrder(PaymentTypeDto paymentType) {
        synchronized (this) {
            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentType.getPid());
            if (Objects.isNull(order)) {
                order = 0;
                PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order);
            } else {
                List<Details> details = findAllByPids(paymentType.getDetails());
                long paymentTypeRequisitesSize = details.stream().filter(d->Boolean.TRUE.equals(d.getIsOn())).count();
                if (order + 1 >= paymentTypeRequisitesSize)
                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), 0);
                else
                    PAYMENT_REQUISITE_ORDER.put(paymentType.getPid(), order + 1);
            }
        }
    }

    public void removeOrder(Long paymentTypePid) {
        synchronized (this) {
            PAYMENT_REQUISITE_ORDER.remove(paymentTypePid);
        }
    }

    @Override
    public String getNotTargetRequisite(PaymentTypeDto paymentType) {
        List<Details> Details = detailsRepository.findAllByPidIn(paymentType.getDetails());
        if (CollectionUtils.isEmpty(Details)) {
            throw new BaseException("Не установлены реквизиты для " + paymentType.getName() + ".");
        }
        if (BooleanUtils.isNotTrue(paymentType.getIsDynamicOn())) {
            return Details.stream()
                    .filter(requisite -> Objects.isNull(requisite.getTargetAmount())
                            || requisite.getTargetAmount() == 0)
                    .filter(requisite -> BooleanUtils.isTrue(requisite.getIsOn()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("Не найден ни один включенный реквизит"))
                    .getRequisite();
        }
        List<Details> turnedRequisites = Details.stream()
                .filter(requisite -> Objects.isNull(requisite.getTargetAmount()) || requisite.getTargetAmount() == 0)
                .filter(requisite -> BooleanUtils.isTrue(requisite.getIsOn()))
                .toList();
        if (CollectionUtils.isEmpty(turnedRequisites))
            throw new BaseException("Не найден ни один включенный реквизит.");
        Integer order = getOrder(paymentType.getPid());
        updateOrder(paymentType);
        return turnedRequisites.get(order).getRequisite();
    }

    @Override
    public Optional<Details> getTarget(/*PaymentType paymentType*/ List<Long> detailIds, Integer amount) {
//        List<Details> targetDetailss =
//                detailsRepository.getByPaymentTypeAndNotEmptyTargetAmount(paymentType.getPid());
        List<Details> targetDetails = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(detailIds);
        if (!targetDetails.isEmpty()) {
            synchronized (TARGET_PAYMENT_REQUISITE_SYNCHRONIZE_OBJECT) {
                for (Details Details : targetDetails) {
                    int targetAmount = Details.getTargetAmount();
                    int receiveAmount = Objects.nonNull(Details.getReceivedAmount()) ? Details.getReceivedAmount() : 0;
                    int reserveAmount = Objects.nonNull(Details.getReserveAmount()) ? Details.getReserveAmount() : 0;
                    if (targetAmount - receiveAmount - reserveAmount >= amount && Details.isInRange(amount)) {
                        Details.setReserveAmount(reserveAmount + amount);
                        detailsRepository.save(Details);
                        return Optional.of(Details);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Details> findAllByPids(List<Long> detailIds){
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
    public void saveReserveAmount(Long detailsId, Integer dealAmount){
        synchronized (TARGET_PAYMENT_REQUISITE_SYNCHRONIZE_OBJECT) {
            Optional<Details> maybeDetails = findByIdOptional(detailsId);
            if (maybeDetails.isPresent()) {
                Details details = maybeDetails.get();
                int reserveAmount = Objects.isNull(details.getReserveAmount()) ? 0 : details.getReserveAmount();
                details.setReserveAmount(reserveAmount - dealAmount);
                detailsRepository.save(details);
            }
        }
    }

    @Override
    public void confirmPayment(Long detailsId, Integer dealAmount){
        synchronized (TARGET_PAYMENT_REQUISITE_SYNCHRONIZE_OBJECT) {
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
    }

    @Override
    public List<Details> saveAll(List<Details> detailsDto) {
        return detailsRepository.saveAll(detailsDto);
    }

}
