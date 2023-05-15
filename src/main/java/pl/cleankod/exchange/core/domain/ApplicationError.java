package pl.cleankod.exchange.core.domain;

public class ApplicationError {

    private final String message;
    private final int httpStatus;

    public ApplicationError(String message, int httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
