package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * Create a Money object from a starting amount, a rate and a currency. The final amount will be divided by the provided rate
     */
    public static Money of(BigDecimal startingAmount, BigDecimal divideRate, Currency currency) {
        return Money.of(computeAmount(startingAmount, divideRate), currency);
    }

    private static BigDecimal computeAmount(BigDecimal amount, BigDecimal rate) {
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        return amount.divide(rate, RoundingMode.HALF_EVEN);
    }

    public Optional<Money> convert(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        if (targetCurrency.equals(currency)) {
            return Optional.of(this);
        }

        return currencyConverter.convert(this, targetCurrency);
    }

}
