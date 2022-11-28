package pl.cleankod.exchange.core.usecase.exception;

import java.util.Currency;

public class CurrencyConversionException extends IllegalStateException {
    public CurrencyConversionException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from [%s] to [%s].", sourceCurrency, targetCurrency));
    }

    public CurrencyConversionException(Currency baseCurrency) {
        super(String.format("Base currency of application set to [%s] exchange operation forbidden.", baseCurrency));
    }

}
