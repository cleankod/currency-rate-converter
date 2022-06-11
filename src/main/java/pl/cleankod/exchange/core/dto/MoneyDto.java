package pl.cleankod.exchange.core.dto;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record MoneyDto(BigDecimal amount, Currency currency) {

    public static MoneyDto of(final BigDecimal amount, final Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new MoneyDto(amount, currency);
    }

    public static MoneyDto of(final String amount, final String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new MoneyDto(new BigDecimal(amount), Currency.getInstance(currency));
    }

}
