package pl.cleankod.exchange.core.exception;

import java.util.Currency;

public class UnavailableExchangeRateException extends IllegalStateException {
    public UnavailableExchangeRateException(Currency sourceCurrency, Currency targetCurrency) {
        super(String.format("Cannot convert currency from %s to %s. Because the exchange rate is currently not available", sourceCurrency, targetCurrency));
    }
}
