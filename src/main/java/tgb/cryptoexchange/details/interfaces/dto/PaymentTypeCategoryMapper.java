package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface PaymentTypeCategoryMapper {

    PaymentTypeCategoryDto toDto(PaymentTypeCategory entity);

    List<PaymentTypeCategoryDto> toDtoList(List<PaymentTypeCategory> entities);

    PaymentTypeCategory toEntity(PaymentTypeCategoryDto paymentTypeCategoryDto);

}
