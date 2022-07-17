package pl.cleankod.exchange.entrypoint.model;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
