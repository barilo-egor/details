package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface DealInfoMapper {

    @Mapping(target = "detailsId", source = "details.pid")
    DealInfoDto toDto(DealInfo entity);

    List<DealInfoDto> toDtoList(List<DealInfo> entities);

    @Mapping(target = "details.pid", source = "detailsId")
    DealInfo toEntity(DealInfoDto dealInfoDto);

}
