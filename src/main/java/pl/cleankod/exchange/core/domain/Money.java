package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.Result;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
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

    public Result<Money, String> convert(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        return currencyConverter.convert(this, targetCurrency);
    }
}