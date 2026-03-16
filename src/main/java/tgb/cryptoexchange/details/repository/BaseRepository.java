package tgb.cryptoexchange.details.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.details.entity.BasePersist;

@Repository
@Transactional
public interface BaseRepository<T extends BasePersist> extends JpaRepository<T, Long> {
}

