package pl.cleankod.exchange.exception;

import pl.cleankod.exchange.exception.error_type.ErrorType;

public class CurrencyConversionException extends RestRuntimeException {
    public CurrencyConversionException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }
}
