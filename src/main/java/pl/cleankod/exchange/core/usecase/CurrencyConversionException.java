package pl.cleankod.exchange.core.usecase;

import java.util.Currency;

public class CurrencyConversionException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public CurrencyConversionException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency));
    }
}
