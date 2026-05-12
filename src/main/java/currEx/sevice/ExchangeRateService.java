package currEx.sevice;

import currEx.dao.CurrencyDao;
import currEx.dao.ExchangeRateDao;
import currEx.dto.ExchangeRateRequestDto;
import currEx.dto.ExchangeRateResponseDto;
import currEx.dto.ExchangeResponseDto;
import currEx.entity.CurrencyEntity;
import currEx.entity.ExchangeRateEntity;
import currEx.mapper.ExchangeRateMapper;
import currEx.sevice.calculator.ExchangeCalculator;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final ExchangeCalculator exchangeCalculator = new ExchangeCalculator();
    private static final String USD_CODE="USD";

    public ExchangeResponseDto exchange(String baseCode, String targetCode, BigDecimal amount) {

        List<ExchangeRateEntity> eRateEntityList = exchangeRateDao
                .findAllRateByCodes(baseCode,targetCode,USD_CODE);
        return exchangeCalculator
                .calculateExchange(eRateEntityList,  baseCode, targetCode,USD_CODE, amount);
    }


    public ExchangeRateResponseDto updateRate(ExchangeRateRequestDto eRateRequestDto) {
        return requestToResponseDto(eRateRequestDto, exchangeRateDao::update);
    }

    public ExchangeRateResponseDto create(ExchangeRateRequestDto eRateRequestDto) {
        return requestToResponseDto(eRateRequestDto,
                entity -> exchangeRateDao.save(entity));
    }

    private ExchangeRateResponseDto requestToResponseDto(ExchangeRateRequestDto eRateRequestDto
            , Function<ExchangeRateEntity, ExchangeRateEntity> func) {
        CurrencyEntity baseCurrencyEntity = currencyDao.findByCode(eRateRequestDto.baseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.findByCode(eRateRequestDto.targetCurrencyCode());
        ExchangeRateEntity eRateEntity = ExchangeRateEntity.builder()
                .baseCurrency(baseCurrencyEntity)
                .targetCurrency(targetCurrencyEntity)
                .rate(eRateRequestDto.rate())
                .build();
        eRateEntity = func.apply(eRateEntity);
        return ExchangeRateMapper.MAPPER.toDto(eRateEntity);

    }

    public List<ExchangeRateResponseDto> getExchangeRates() {
        List<ExchangeRateEntity> eRateEntityList = exchangeRateDao.findAll();
        List<ExchangeRateResponseDto> eRateResponseDtoList = new ArrayList<>();
        for (ExchangeRateEntity eRateEntity : eRateEntityList) {
            ExchangeRateResponseDto eRateResponseDto = ExchangeRateMapper.MAPPER.toDto(eRateEntity);
            eRateResponseDtoList.add(eRateResponseDto);
        }
        return eRateResponseDtoList;
    }

    public ExchangeRateResponseDto getExchangeRate(String baseCode, String targetCode) {
        return ExchangeRateMapper.MAPPER.toDto(exchangeRateDao.findByCodes(baseCode, targetCode));
    }
}
