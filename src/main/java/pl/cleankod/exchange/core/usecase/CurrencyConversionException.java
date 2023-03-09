package pl.cleankod.exchange.core.usecase;


import pl.cleankod.exchange.core.domain.Currency;

public class CurrencyConversionException extends IllegalStateException {
    public CurrencyConversionException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency));
    }
}
