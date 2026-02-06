package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DetailsMapper {

    DetailsDto toDto(Details entity);

    List<DetailsDto> toDtoList(List<Details> entities);

    Details toEntity(DetailsDto detailsDto);

    void updateEntityFromDto(DetailsDto dto, @MappingTarget Details entity);

}
