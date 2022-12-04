package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public interface CurrencyConversionService {
    Result<Money, AccountRetrievalFailedReason> convert(Money money, Currency targetCurrency) throws RuntimeException;

    default BigDecimal calculate(BigDecimal value, BigDecimal rate) {
        // TODO I'd actually add some configurable price rate for exchange if you really want to earn anything :)
        return value.divide(rate, RoundingMode.HALF_DOWN);
    }
}
