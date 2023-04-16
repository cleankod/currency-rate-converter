package pl.cleankod.exchange.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.ExchangeRateNotAvailableException;
import pl.cleankod.util.UnknownCurrencyException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            UnknownCurrencyException.class,
            IllegalArgumentException.class,
    })
    protected ResponseEntity<ApiError> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({ExchangeRateNotAvailableException.class})
    protected ResponseEntity<ApiError> handleServiceUnavailable(Exception ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("Service is not available."));
    }
}
