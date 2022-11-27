package pl.cleankod.exchange.entrypoint.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CurrencyRateConverterException {

    public NotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
