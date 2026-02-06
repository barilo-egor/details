package tgb.cryptoexchange.details.interfaces.service;

import org.springframework.web.bind.annotation.RequestParam;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;

import java.util.List;
import java.util.Optional;

public interface IDetailsService extends IBasePersistService<Details> {

    void updateRequisiteByPid(String requisite, Long pid);

    List<Details> getWithNotEmptyTargetAmount();

    Optional<Details> findByIdOptional(Long pid);

    Optional<Details> getTarget(List<Long> detailIds, Integer amount);

    String getNotTargetRequisite(PaymentTypeDto paymentType);

    List<Details> findAllByPids(List<Long> detailIds);

//    Integer getOrder(Long paymentTypeId);

//    void updateOrder(PaymentTypeDto paymentType);
//
//    void checkOrder(PaymentTypeDto paymentType);
//
//    void removeOrder(Long paymentTypeId);

    void saveReserveAmount(Long detailsId, Integer dealAmount);

    void confirmPayment(Long detailsId, Integer dealAmount);

    List<Details> saveAll(List<Details> detailsDto);

    void patchDetails(Long pid, DetailsDto dto);

}
