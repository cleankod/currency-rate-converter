package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.ApplicationError;

import java.util.Currency;

public class CurrencyConversionError extends ApplicationError {

    public CurrencyConversionError(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency), 400);
    }
}
