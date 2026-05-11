package currEx.mapper;

import currEx.dto.ExchangeRateResponseDto;
import currEx.entity.ExchangeRateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper MAPPER= Mappers.getMapper(ExchangeRateMapper.class);
    ExchangeRateResponseDto toDto(ExchangeRateEntity exchangeRateEntity);

}
