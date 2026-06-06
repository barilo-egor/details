package tgb.cryptoexchange.details.interfaces.service;

import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;

import java.util.List;

public interface IPaymentTypeCategoryService extends IBasePersistService<PaymentTypeCategory> {

    List<PaymentTypeCategory> findAll();

    PaymentTypeCategory save(PaymentTypeCategoryDto paymentTypeCategoryDto);

}
