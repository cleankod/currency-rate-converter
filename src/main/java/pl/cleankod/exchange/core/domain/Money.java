package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    public static final int NPB_API_PRECISION = 2;

    public static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount, BigDecimal rate, Currency currency) {
        Preconditions.requireNonNull(rate);
        amount = amount.setScale(NPB_API_PRECISION, RoundingMode.HALF_EVEN);
        amount = amount.divide(rate, RoundingMode.HALF_EVEN);
        return Money.of(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(new BigDecimal(amount), Currency.getInstance(currency));
    }

    public Money convert(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        return currencyConverter.convert(this, targetCurrency);
    }
}
