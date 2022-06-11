package pl.cleankod.exchange.exception.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.cleankod.exchange.core.aggregator.AccountAggregatorImpl;
import pl.cleankod.exchange.exception.CurrencyConversionException;
import pl.cleankod.exchange.exception.InvalidDataException;
import pl.cleankod.exchange.exception.ResourceNotFoundException;
import pl.cleankod.exchange.exception.RestRuntimeException;
import pl.cleankod.exchange.exception.error.ErrorMessage;

import static org.springframework.http.HttpStatus.*;
import static pl.cleankod.exchange.exception.error.ErrorMessageBuilder.buildExceptionMessage;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(AccountAggregatorImpl.class);

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handle(final Exception exception) {
        LOGGER.error("Internal exception!", exception);
        return buildExceptionMessage(null, null, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handle(final RestRuntimeException exception) {
        LOGGER.error("Internal exception!", exception);
        return buildExceptionMessage(exception.getErrorType(), exception.getMessage(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handle(final InvalidDataException exception) {
        LOGGER.warn("Returned bad request with error type {} and message: {}", exception.getErrorType(), exception.getMessage());
        return buildExceptionMessage(exception.getErrorType(), exception.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handle(final CurrencyConversionException exception) {
        LOGGER.warn("Returned bad request with error type {} and message: {}", exception.getErrorType(), exception.getMessage());
        return buildExceptionMessage(exception.getErrorType(), exception.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handle(final ResourceNotFoundException exception) {
        LOGGER.warn("Resource not found exception!", exception);
        return buildExceptionMessage(exception.getErrorType(), exception.getMessage(), NOT_FOUND);
    }

}
