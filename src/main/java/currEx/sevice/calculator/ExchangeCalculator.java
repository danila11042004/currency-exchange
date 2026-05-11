package currEx.sevice.calculator;

import currEx.dto.CurrencyResponseDto;
import currEx.dto.ExchangeResponseDto;
import currEx.entity.CurrencyEntity;
import currEx.entity.ExchangeRateEntity;
import currEx.mapper.CurrencyMapper;
import exeptions.ExchangeNotPossibleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeCalculator {
    private static final BigDecimal NUMBER_TO_ROUND = new BigDecimal("0.01");
    public ExchangeResponseDto calculateExchange(List<ExchangeRateEntity> eRateEntityList
            , String baseCode, String targetCode
            , String additionalCode, BigDecimal amount) {

        return getDirectRate(eRateEntityList, baseCode, targetCode, amount)
                .or(() -> getRateUsingReverse(eRateEntityList, baseCode, targetCode, amount))
                .or(() -> getRateViaAdditionalCode(eRateEntityList, additionalCode, baseCode, targetCode, amount))
                .orElseThrow(() -> new ExchangeNotPossibleException("Для данных валют обмен невозможен"));

    }

    private Optional<ExchangeResponseDto> getDirectRate(List<ExchangeRateEntity> eRateEntityList
            , String baseCode, String targetCode, BigDecimal amount) {
        return eRateEntityList.stream()
                .filter(entity ->
                        entity.getBaseCurrency().getCode().equals(baseCode)
                                && entity.getTargetCurrency().getCode().equals(targetCode))
                .findFirst()
                .map(entity -> {
                    BigDecimal convertedAmount = entity.getRate().multiply(amount);
                    if(convertedAmount.compareTo(NUMBER_TO_ROUND)>=0){
                        convertedAmount=convertedAmount.setScale(2,RoundingMode.HALF_UP);
                    }
                    convertedAmount=convertedAmount.stripTrailingZeros();
                    return getExchangeDto(entity.getRate(), entity.getBaseCurrency(),
                            entity.getTargetCurrency(), amount, convertedAmount);
                });
    }

    private Optional<ExchangeResponseDto> getRateUsingReverse(List<ExchangeRateEntity> eRateEntityList
            , String baseCode, String targetCode, BigDecimal amount) {
        return eRateEntityList.stream()
                .filter(entity ->
                        entity.getBaseCurrency().getCode().equals(targetCode)
                                && entity.getTargetCurrency().getCode().equals(baseCode))
                .findFirst()
                .map(entity -> {
                    BigDecimal rate = BigDecimal.ONE.divide(entity.getRate(),
                            6, RoundingMode.HALF_UP);
                    BigDecimal convertedAmount = rate.multiply(amount);
                    if(convertedAmount.compareTo(NUMBER_TO_ROUND)>=0){
                        convertedAmount=convertedAmount.setScale(2,RoundingMode.HALF_UP);
                    }
                    convertedAmount=convertedAmount.stripTrailingZeros();
                    return getExchangeDto(entity.getRate(), entity.getTargetCurrency(),
                            entity.getBaseCurrency(), amount, convertedAmount);
                });

    }

    private Optional<ExchangeResponseDto> getRateViaAdditionalCode(List<ExchangeRateEntity> rateList
            , String additionalCode, String baseCode, String targetCode, BigDecimal amount) {

        Optional<ExchangeRateEntity> usdToBaseERate = rateList.stream()
                .filter(entity ->
                        entity.getBaseCurrency().getCode().equals(additionalCode) &&
                                entity.getTargetCurrency().getCode().equals(baseCode))
                .findFirst();
        Optional<ExchangeRateEntity> usdToTargetERate = rateList.stream()
                .filter(entity ->
                        entity.getBaseCurrency().getCode().equals(additionalCode) &&
                                entity.getTargetCurrency().getCode().equals(targetCode))
                .findFirst();
        if (usdToBaseERate.isPresent() && usdToTargetERate.isPresent()) {
            BigDecimal usdToBaseCourse = usdToBaseERate.get().getRate();
            BigDecimal usdToTargetCourse = usdToTargetERate.get().getRate();
            BigDecimal rate = usdToTargetCourse.divide(usdToBaseCourse, 6,
                    RoundingMode.HALF_UP);
            BigDecimal convertedAmount = rate.multiply(amount);
            if(convertedAmount.compareTo(NUMBER_TO_ROUND)>=0){
                convertedAmount=convertedAmount.setScale(2,RoundingMode.HALF_UP);
            }
            convertedAmount=convertedAmount.stripTrailingZeros();
            return Optional.of(getExchangeDto(rate, usdToBaseERate.get().getTargetCurrency()
                    , usdToTargetERate.get().getTargetCurrency(), amount, convertedAmount));
        }
        return Optional.empty();
    }


    private ExchangeResponseDto getExchangeDto(BigDecimal rate
            , CurrencyEntity baseCurrencyEntity, CurrencyEntity targetCurrencyEntity
            , BigDecimal amount, BigDecimal convertedAmount) {
        CurrencyResponseDto baseCurrency = CurrencyMapper.MAPPER.toDto(baseCurrencyEntity);
        CurrencyResponseDto targetCurrency = CurrencyMapper.MAPPER.toDto(targetCurrencyEntity);
        return new ExchangeResponseDto(baseCurrency, targetCurrency, rate,
                amount, convertedAmount);
    }
}
