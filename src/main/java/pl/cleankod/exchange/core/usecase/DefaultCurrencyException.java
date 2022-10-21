package pl.cleankod.exchange.core.usecase;

public class DefaultCurrencyException extends RuntimeException {

    public DefaultCurrencyException(String targetCurrency) {
        super(String.format("Service rates provider is down and we could not found any local rate for currency: %s.", targetCurrency));
    }
}
