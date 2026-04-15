package tgb.cryptoexchange.details.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.interfaces.dto.DealInfoMapper;
import tgb.cryptoexchange.details.interfaces.service.IDealInfoService;
import tgb.cryptoexchange.details.repository.BaseRepository;
import tgb.cryptoexchange.details.repository.DealInfoRepository;

@Service
@Slf4j
public class DealInfoService extends BasePersistService<DealInfo> implements IDealInfoService {

    private final DealInfoRepository dealInfoRepository;

    private final DealInfoMapper mapper;

    public DealInfoService(DealInfoRepository dealInfoRepository, DealInfoMapper mapper) {
        this.dealInfoRepository = dealInfoRepository;
        this.mapper = mapper;
    }

    @Override
    public DealInfo save(DealInfoDto dealInfoDto) {
        DealInfo dealInfo = mapper.toEntity(dealInfoDto);
        return dealInfoRepository.save(dealInfo);
    }

    @Override
    public Page<DealInfo> findAll(DealInfoDto dto, Pageable pageable) {
        DealInfo probe = mapper.toEntity(dto);
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues();
        Example<DealInfo> example = Example.of(probe, matcher);
        return dealInfoRepository.findAll(example, pageable);
    }


    @Override
    protected BaseRepository<DealInfo> getBaseRepository() {
        return dealInfoRepository;
    }
}
