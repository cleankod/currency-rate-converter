package pl.cleankod.exchange.core.exceptions;

public class RateRetrievalException extends RuntimeException {
    public RateRetrievalException(String exceptionMsg) {
        super(exceptionMsg);
    }

    public RateRetrievalException(String currencyCode, Exception exception) {
        super(String.format("Could not fetch rates for currency %s", currencyCode), exception);
    }
}
