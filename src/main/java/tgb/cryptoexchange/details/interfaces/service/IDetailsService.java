package tgb.cryptoexchange.details.interfaces.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;

import java.util.List;

public interface IDetailsService extends IBasePersistService<Details> {

    void updateRequisiteByPid(String requisite, Long pid);

    Page<Details> getWithNotEmptyTargetAmount(Pageable pageable);

    Details findById(Long pid);

    Details getTarget(List<Long> detailIds, Integer amount, Boolean isOn);

    String getNotTargetRequisite(PaymentTypeDto paymentType, Boolean isOn);

    Page<Details> findAllByPids(List<Long> detailIds, Pageable pageable);

    void saveReserveAmount(Long detailsId, Integer dealAmount);

    Details confirmPayment(Long detailsId, Integer dealAmount);

    List<Details> saveAll(List<Details> detailsDto);

    void patchDetails(Long pid, DetailsDto dto, boolean updateNotNull);

    Page<Details> findAll(Pageable pageable);

}
