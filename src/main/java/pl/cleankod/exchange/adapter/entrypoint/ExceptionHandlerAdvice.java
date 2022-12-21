package pl.cleankod.exchange.adapter.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exception.CurrencyConversionUnsupportedException;
import pl.cleankod.exchange.adapter.entrypoint.model.ApiError;
import pl.cleankod.exchange.core.exception.ExchangeRateNotApplicableException;
import pl.cleankod.exchange.core.exception.UnavailableExchangeRateException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionUnsupportedException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionUnsupportedException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            UnavailableExchangeRateException.class
    })
    protected ResponseEntity<ApiError> handleUnavailableExchangeRate(UnavailableExchangeRateException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            ExchangeRateNotApplicableException.class
    })
    protected ResponseEntity<ApiError> handleExchangeRateNotApplicable(ExchangeRateNotApplicableException ex) {
        return ResponseEntity.badRequest().body(new ApiError("Please contact your administrator" ));
    }

}
