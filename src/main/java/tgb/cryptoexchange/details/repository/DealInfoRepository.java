package tgb.cryptoexchange.details.repository;

import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.details.entity.DealInfo;

@Repository
@Transactional
public interface DealInfoRepository extends BaseRepository<DealInfo>, QueryByExampleExecutor<DealInfo> {

}
