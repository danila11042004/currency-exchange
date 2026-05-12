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
    
}
