package pl.cleankod.exchange.core.domain;

import java.util.Currency;

public interface CurrencyConverter {
    Money convert(Money money, Currency targetCurrency);
}
