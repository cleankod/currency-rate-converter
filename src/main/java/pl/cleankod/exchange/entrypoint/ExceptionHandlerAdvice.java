package pl.cleankod.exchange.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exceptions.CurrencyConversionException;
import pl.cleankod.exchange.core.exceptions.RateRetrievalException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class,
            RateRetrievalException.class,
            Exception.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    protected ResponseEntity<ApiError> handleBadRequest(RateRetrievalException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }
}
