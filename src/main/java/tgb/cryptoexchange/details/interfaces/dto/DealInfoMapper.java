package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DealInfoMapper {

    DealInfoDto toDto(DealInfo entity);

    List<DealInfoDto> toDtoList(List<DealInfo> entities);

    DealInfo toEntity(DealInfoDto dealInfoDto);

}
