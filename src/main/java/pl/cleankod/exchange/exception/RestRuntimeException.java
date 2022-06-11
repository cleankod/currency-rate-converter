package pl.cleankod.exchange.exception;

import pl.cleankod.exchange.exception.error_type.ErrorType;


public class RestRuntimeException extends RuntimeException {

    private final ErrorType errorType;

    public RestRuntimeException(final ErrorType errorType, final String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

}
