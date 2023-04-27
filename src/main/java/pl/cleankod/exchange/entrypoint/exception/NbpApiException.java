package pl.cleankod.exchange.entrypoint.exception;

public class NbpApiException extends RuntimeException {
    public NbpApiException(String currency) {
        super(String.format("Could not fetch rates for given currency %s", currency));
    }
}
