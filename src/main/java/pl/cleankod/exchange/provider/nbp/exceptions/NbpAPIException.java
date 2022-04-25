package pl.cleankod.exchange.provider.nbp.exceptions;

public class NbpAPIException extends RuntimeException {
    private static final String message = "Error connecting to NBP API";

    public NbpAPIException() {
        super(message);
    }
}
