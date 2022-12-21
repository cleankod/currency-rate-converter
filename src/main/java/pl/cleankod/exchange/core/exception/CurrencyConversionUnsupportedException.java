package pl.cleankod.exchange.core.exception;

import java.util.Currency;

public class CurrencyConversionUnsupportedException extends IllegalStateException {
    public CurrencyConversionUnsupportedException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency));
    }
}
