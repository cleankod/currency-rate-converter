package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

import static java.math.RoundingMode.HALF_EVEN;


public sealed interface Money permits FractionalMoney, WholeMoney {
    BigDecimal amount();

    Currency currency();

    static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);

        return WholeMoney.isWhole(amount, currency) ? new WholeMoney(amount, currency) : new FractionalMoney(amount, currency);
    }

    static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return of(new BigDecimal(amount), Currency.getInstance(currency));
    }

    default Money convert(ExchangeRate exchangeRate) {
        Preconditions.requireNonNull(exchangeRate);
        if (!currency().equals(exchangeRate.source())) {
            throw new IllegalArgumentException("Exchange rate source currency does not match the money currency.");
        }

        BigDecimal convertedAmount = amount().multiply(exchangeRate.rate());
        return Money.of(convertedAmount, exchangeRate.target());
    }

    default WholeMoney roundToWhole() {
        int scale = currency().getDefaultFractionDigits();
        BigDecimal roundedAmount = amount().setScale(scale, HALF_EVEN);
        return WholeMoney.of(roundedAmount, currency());
    }
}

