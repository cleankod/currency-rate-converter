package pl.cleankod.exchange.core.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "BadRequestException: " + getMessage();
    }
}
