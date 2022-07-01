package pl.cleankod.exchange.entrypoint;

public class AccountOperationException extends RuntimeException {
    private final int statusCode;

    public AccountOperationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
