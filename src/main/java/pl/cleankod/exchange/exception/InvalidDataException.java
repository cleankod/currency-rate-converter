package pl.cleankod.exchange.exception;

import pl.cleankod.exchange.exception.error_type.ErrorType;

public class InvalidDataException extends RestRuntimeException {

    public InvalidDataException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
