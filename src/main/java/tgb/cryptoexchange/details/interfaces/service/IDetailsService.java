package tgb.cryptoexchange.details.interfaces.service;

import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;

import java.util.List;

public interface IDetailsService extends IBasePersistService<Details> {

    void updateRequisiteByPid(String requisite, Long pid);

    List<Details> getWithNotEmptyTargetAmount();

    Details findById(Long pid);

    Details getTarget(List<Long> detailIds, Integer amount, Boolean isOn);

    String getNotTargetRequisite(PaymentTypeDto paymentType, Boolean isOn);

    List<Details> findAllByPids(List<Long> detailIds);

    void saveReserveAmount(Long detailsId, Integer dealAmount);

    Details confirmPayment(Long detailsId, Integer dealAmount);

    List<Details> saveAll(List<Details> detailsDto);

    void patchDetails(Long pid, DetailsDto dto, boolean updateNotNull);

}
