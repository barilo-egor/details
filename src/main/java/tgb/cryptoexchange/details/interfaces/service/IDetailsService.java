package tgb.cryptoexchange.details.interfaces.service;

import org.springframework.web.bind.annotation.RequestParam;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;

import java.util.List;
import java.util.Optional;

public interface IDetailsService extends IBasePersistService<Details> {

    void updateRequisiteByPid(String requisite, Long pid);

    List<Details> getWithNotEmptyTargetAmount();

    Optional<Details> findByIdOptional(Long pid);

    Optional<Details> getTarget(List<Long> detailIds, Integer amount);

    List<Details> findAllByPids(List<Long> detailIds);

    Integer getOrder(Long paymentTypeId);

    void updateOrder(Long paymentTypeId, List<Long> detailIds);

    void checkOrder(Long paymentTypeId, List<Long> detailIds);

    void removeOrder(Long paymentTypeId);

    void saveReserveAmount(Long detailsId, Integer dealAmount);

    void confirmPayment(Long detailsId, Integer dealAmount);

}
