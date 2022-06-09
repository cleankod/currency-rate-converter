package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
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

    public Money convert(Currency targetCurrency, Currency baseCurrency, RateWrapper.MidRate midRate) {
        if (!baseCurrency.equals(targetCurrency)) {
            return convertTo(targetCurrency, midRate);
        }

        if (!currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(currency(), targetCurrency);
        }

        return this;
    }

    private Money convertTo(Currency targetCurrency, RateWrapper.MidRate midRate) {
        return new Money(amount().divide(midRate.rate(), RoundingMode.HALF_UP), targetCurrency);
    }
}
