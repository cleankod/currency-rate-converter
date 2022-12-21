package pl.cleankod.exchange.adapter.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.adapter.entrypoint.model.ApiError;
import pl.cleankod.exchange.core.exception.UnavailableExchangeRateException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            UnavailableExchangeRateException.class
    })
    protected ResponseEntity<ApiError> handleUnavailableExchangeRate(UnavailableExchangeRateException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

}
