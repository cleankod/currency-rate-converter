package pl.cleankod.exchange.entrypoint.model;

import java.util.Currency;

public class CurrencyConversionException extends IllegalStateException {
    public CurrencyConversionException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency));
    }

    public CurrencyConversionException(String targetCurrency) {
        super(String.format("The requested currency %s is unsupported.", targetCurrency));
    }

}
