package pl.cleankod.exchange.entrypoint.exception;

import org.springframework.http.HttpStatus;

public class CurrencyRateConverterException extends RuntimeException {
    public static final String UNKNOWN_ERROR_MESSAGE = "Internal Error";
    public static final String EXTERNAL_SERVICE_UNAVAILABLE = "External service is currently unavailable";

    protected final HttpStatus httpStatus;

    public CurrencyRateConverterException(final String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CurrencyRateConverterException(final String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}
