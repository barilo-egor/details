package tgb.cryptoexchange.details.interfaces.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;

public interface IDealInfoService extends IBasePersistService<DealInfo> {

    DealInfo save(DealInfoDto dealInfoDto);

    Page<DealInfo> findAll(DealInfoDto dto, Pageable pageable);
}
