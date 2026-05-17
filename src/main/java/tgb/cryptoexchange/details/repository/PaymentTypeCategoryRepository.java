package tgb.cryptoexchange.details.repository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;

@Repository
@Transactional
public interface PaymentTypeCategoryRepository extends BaseRepository<PaymentTypeCategory>{
}
