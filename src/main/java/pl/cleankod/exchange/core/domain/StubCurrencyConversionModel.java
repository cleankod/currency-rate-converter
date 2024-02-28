package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record StubCurrencyConversionModel(String from, String to) {

    public static String of(String from, String to) {
        Preconditions.requireNonNull(from);
        Preconditions.requireNonNull(to);
        return from + "_" + to;
    }

}
