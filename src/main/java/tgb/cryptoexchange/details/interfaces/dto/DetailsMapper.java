package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.*;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DetailsMapper {

    DetailsDto toDto(Details entity);

    List<DetailsDto> toDtoList(List<Details> entities);

    @Mapping(target = "lastAccessedAt", ignore = true)
    Details toEntity(DetailsDto detailsDto);

    @Mapping(target = "lastAccessedAt", ignore = true)
    @Mapping(target = "pid", ignore = true)
    void updateEntityFromDto(DetailsDto dto, @MappingTarget Details entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "lastAccessedAt", ignore = true)
    @Mapping(target = "pid", ignore = true)
    void updateEntityFromDtoNotNull(DetailsDto dto, @MappingTarget Details entity);

}
