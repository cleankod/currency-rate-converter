package pl.cleankod.exchange.core.exception;

public class UnknownCurrencyException extends IllegalArgumentException {

    public UnknownCurrencyException(String currency) {
        super(String.format("Cannot find currency %s.", currency));
    }

}
