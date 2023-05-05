package pl.cleankod.exchange.core.domain;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record WholeMoney(BigDecimal amount, Currency currency) implements Money {
    public WholeMoney {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);

        if (!isWhole(amount, currency)) {
            throw new IllegalArgumentException(String.format("%s %s is not whole money", amount, currency));
        }

        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    public static WholeMoney of(BigDecimal amount, Currency currency) {
        return new WholeMoney(amount, currency);
    }

    public static WholeMoney of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return of(new BigDecimal(amount), Currency.getInstance(currency));
    }

    static boolean isWhole(BigDecimal amount, Currency currency) {
        BigDecimal unscaledAmount = amount.movePointRight(currency.getDefaultFractionDigits());
        BigDecimal strippedUnscaledAmount = unscaledAmount.stripTrailingZeros();
        return strippedUnscaledAmount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }
}
