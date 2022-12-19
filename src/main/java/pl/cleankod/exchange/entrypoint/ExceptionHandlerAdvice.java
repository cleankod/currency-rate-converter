package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exceptions.BadRequestException;
import pl.cleankod.exchange.core.exceptions.NotFoundException;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleCurrencyConversionException(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    protected ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({
            BadRequestException.class,
    })
    protected ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }
}
