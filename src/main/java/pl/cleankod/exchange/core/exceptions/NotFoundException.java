package pl.cleankod.exchange.core.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "NotFoundException: " + getMessage();
    }
}
