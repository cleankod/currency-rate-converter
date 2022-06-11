package pl.cleankod.exchange.exception;

import pl.cleankod.exchange.exception.error_type.ErrorType;

public class ResourceNotFoundException extends RestRuntimeException {

    public ResourceNotFoundException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
