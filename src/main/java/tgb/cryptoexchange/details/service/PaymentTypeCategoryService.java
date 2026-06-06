package tgb.cryptoexchange.details.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeCategoryMapper;
import tgb.cryptoexchange.details.interfaces.service.IPaymentTypeCategoryService;
import tgb.cryptoexchange.details.repository.BaseRepository;
import tgb.cryptoexchange.details.repository.PaymentTypeCategoryRepository;

import java.util.List;

@Service
@Slf4j
public class PaymentTypeCategoryService extends BasePersistService<PaymentTypeCategory> implements IPaymentTypeCategoryService {

    private final PaymentTypeCategoryRepository paymentTypeCategoryRepository;

    private final PaymentTypeCategoryMapper mapper;

    public PaymentTypeCategoryService(PaymentTypeCategoryRepository paymentTypeCategoryRepository,
                                      PaymentTypeCategoryMapper mapper) {
        this.paymentTypeCategoryRepository = paymentTypeCategoryRepository;
        this.mapper = mapper;
    }

    @Override
    protected BaseRepository<PaymentTypeCategory> getBaseRepository() {
        return paymentTypeCategoryRepository;
    }

    @Override
    public List<PaymentTypeCategory> findAll() {
        return paymentTypeCategoryRepository.findAll();
    }

    @Override
    public PaymentTypeCategory save(PaymentTypeCategoryDto paymentTypeCategoryDto) {
        PaymentTypeCategory paymentTypeCategory = mapper.toEntity(paymentTypeCategoryDto);
        return paymentTypeCategoryRepository.save(paymentTypeCategory);
    }

}
