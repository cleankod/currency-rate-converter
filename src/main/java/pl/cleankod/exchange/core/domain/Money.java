package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    public static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(new BigDecimal(amount), Currency.getInstance(currency));
    }

    public static Money convert(BigDecimal amount, BigDecimal exchangeRate, Currency currency) {
            BigDecimal calculatedRate = amount.divide(exchangeRate, 2, RoundingMode.HALF_EVEN);
            return new Money(calculatedRate, currency);
    }

    public Money convert(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        return currencyConverter.convert(this, targetCurrency);
    }
}
