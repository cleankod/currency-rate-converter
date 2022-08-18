package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.provider.FixedCurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

public record Money(BigDecimal amount, Currency currency) {

    public static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        Preconditions.requirePositiveAmount(amount);
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return Money.of(new BigDecimal(amount), Currency.getInstance(currency));
    }

    public Optional<Money> convert(FixedCurrencyConversionService currencyConverter, Currency targetCurrency) {
        if (targetCurrency.equals(currency)) {
            return Optional.of(this);
        }

        return currencyConverter.convert(this, targetCurrency);
    }
}
