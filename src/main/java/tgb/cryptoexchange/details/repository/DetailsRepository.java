package tgb.cryptoexchange.details.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;

@Repository
@Transactional
public interface DetailsRepository extends BaseRepository<Details> {

    @Modifying
    @Query("update Details set requisite=:requisite where pid=:pid")
    void updateRequisiteByPid(String requisite, Long pid);

    @Query("from Details where targetAmount is not null and targetAmount > 0")
    List<Details> getWithNotEmptyTargetAmount();

    @Query("from Details d where d.pid in :pids and d.targetAmount is not null and d.targetAmount > 0")
    List<Details> findAllByPidInAndTargetAmountNotEmpty(@Param("pids") List<Long> pids);

    List<Details> findAllByPidIn(List<Long> pids);

}
