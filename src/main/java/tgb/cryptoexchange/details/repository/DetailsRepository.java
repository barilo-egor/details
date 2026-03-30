package tgb.cryptoexchange.details.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DetailsRepository extends BaseRepository<Details> {

    @Modifying
    @Query("update Details set requisite=:requisite where pid=:pid")
    void updateRequisiteByPid(String requisite, Long pid);

    @Query("from Details where targetAmount is not null and targetAmount > 0")
    Page<Details> getWithNotEmptyTargetAmount(Pageable pageable);

    @Query("from Details d where d.pid in :pids and (d.isOn = :isOn or (:isOn = false and d.isOn is null)) and d.targetAmount is not null and d.targetAmount > 0")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    List<Details> findAllByPidInAndTargetAmountNotEmpty(@Param("pids") List<Long> pids, @Param("isOn") Boolean isOn);

    Page<Details> findAllByPidIn(List<Long> pids, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            //Skip lock пропустит уже заблокированную строку и возьмет следующую
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
    @Query("SELECT d FROM Details d " +
            "WHERE d.pid IN :ids " +
            "AND (d.isOn = :isOn OR (:isOn = false AND d.isOn IS NULL)) " +
            "AND (d.targetAmount IS NULL OR d.targetAmount = 0) " +
            "ORDER BY d.lastAccessedAt ASC LIMIT 1")
    Optional<Details> findOldestAvailableDetail(@Param("ids") List<Long> ids, @Param("isOn") Boolean isOn);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    @NonNull
    Optional<Details> findById(@NonNull Long id);
}
