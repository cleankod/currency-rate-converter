package pl.cleankod.exchange.core.usecase;

public class CurrencyNotFoundException extends RuntimeException{

    public CurrencyNotFoundException(String targetCurrency) {
        super(String.format("Could not find any currency type you provided: %s.", targetCurrency));
    }
}
