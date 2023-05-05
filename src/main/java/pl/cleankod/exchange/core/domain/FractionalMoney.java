package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record FractionalMoney(BigDecimal amount, Currency currency) implements Money {
    public FractionalMoney {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);

        if (WholeMoney.isWhole(amount, currency)) {
            throw new IllegalArgumentException(String.format("%s %s is whole money", amount, currency));
        }
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new FractionalMoney(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return of(new BigDecimal(amount), Currency.getInstance(currency));
    }
}
