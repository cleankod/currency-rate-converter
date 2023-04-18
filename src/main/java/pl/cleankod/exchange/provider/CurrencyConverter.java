package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConverter {
    public Money convert(Money money, Currency targetCurrency, BigDecimal exchangeRate) {
        BigDecimal calculatedRate = money.amount().divide(exchangeRate, targetCurrency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }

}
