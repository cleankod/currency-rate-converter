package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;

public record ExchangeRate(Currency source, Currency target, BigDecimal rate) {
    private static int MAX_REASONABLE_SCALE = 6;

    public ExchangeRate {
        Preconditions.requireNonNull(source);
        Preconditions.requireNonNull(target);
        Preconditions.requireNonNull(rate);
    }

    public static ExchangeRate of(Currency source, Currency target, BigDecimal rate) {
        return new ExchangeRate(source, target, rate);
    }

    public static ExchangeRate of(String source, String target, String rate) {
        Preconditions.requireNonNull(source);
        Preconditions.requireNonNull(target);
        Preconditions.requireNonNull(rate);
        return of(Currency.getInstance(source), Currency.getInstance(target), new BigDecimal(rate));
    }

    public ExchangeRate inverse() {
        BigDecimal invertedRate = ONE.divide(rate, MAX_REASONABLE_SCALE, HALF_EVEN);
        return new ExchangeRate(target, source, invertedRate);
    }
}
