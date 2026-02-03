package tgb.cryptoexchange.details.interfaces.dto;

import org.mapstruct.Mapper;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetailsMapper {

    DetailsDto toDto(Details entity);

    List<DetailsDto> toDtoList(List<Details> entities);

    Details toEntity(DetailsDto detailsDto);

}
