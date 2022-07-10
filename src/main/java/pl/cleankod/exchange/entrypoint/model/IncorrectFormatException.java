package pl.cleankod.exchange.entrypoint.model;

public class IncorrectFormatException extends Exception {
    public static final String INCORRECT_UUID_FORMAT = "The given id does not have the correct format.";

    public IncorrectFormatException(String message) {
        super(message);
    }
}
