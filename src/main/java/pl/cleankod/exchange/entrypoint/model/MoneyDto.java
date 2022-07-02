package pl.cleankod.exchange.entrypoint.model;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record MoneyDto(BigDecimal amount, Currency currency) {

    public static MoneyDto of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new MoneyDto(new BigDecimal(amount), Currency.getInstance(currency));
    }
}