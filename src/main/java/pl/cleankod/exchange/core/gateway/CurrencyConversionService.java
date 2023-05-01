package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public interface CurrencyConversionService {

    default Money convert(Money money, Currency targetCurrency) {
        if (money.currency().equals(targetCurrency)) {
            return money;
        }
        if (money.amount().compareTo(BigDecimal.ZERO) == 0) {
            return new Money(BigDecimal.ZERO, targetCurrency);
        }
        BigDecimal rate = getRate(money.currency(), targetCurrency);
        BigDecimal convertedAmount = money.amount().multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
        return new Money(convertedAmount, targetCurrency);
    }

    BigDecimal getRate(Currency currency, Currency targetCurrency);
}
