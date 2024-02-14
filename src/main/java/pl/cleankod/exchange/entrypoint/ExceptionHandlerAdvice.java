package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exceptions.CurrencyConversionException;
import pl.cleankod.exchange.core.exceptions.RateRetrievalException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class,
            RateRetrievalException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    protected ResponseEntity<ApiError> handleBadRequest(RateRetrievalException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }
}
