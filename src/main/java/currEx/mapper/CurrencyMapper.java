package currEx.mapper;

import currEx.dto.CurrencyRequestDto;
import currEx.dto.CurrencyResponseDto;
import currEx.entity.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyMapper {

    CurrencyMapper MAPPER= Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source="fullName",target = "name")
    CurrencyResponseDto toDto(CurrencyEntity currencyEntity);

    @Mapping(source = "name",target = "fullName")
    CurrencyEntity toEntity(CurrencyRequestDto currencyRequestDto);
}
