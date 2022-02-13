package pl.cleankod.exchange.core.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public static Money of(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Given value cannot be null");
        Objects.requireNonNull(currency, "Given value cannot be null");
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Objects.requireNonNull(amount, "Given value cannot be null");
        Objects.requireNonNull(currency, "Given value cannot be null");
        return new Money(new BigDecimal(amount), Currency.getInstance(currency));
    }

    public Money convert(CurrencyConverter currencyConverter, Currency targetCurrency) {
        return currencyConverter.convert(this, targetCurrency);
    }
}
