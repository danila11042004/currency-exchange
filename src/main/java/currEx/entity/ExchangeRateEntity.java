package currEx.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExchangeRateEntity {
    Long id;
    CurrencyEntity baseCurrency;
    CurrencyEntity targetCurrency;
    BigDecimal rate;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ExchangeRateEntity entity)) return false;
        return Objects.equals(baseCurrency, entity.baseCurrency) && Objects.equals(targetCurrency, entity.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency);
    }
}
