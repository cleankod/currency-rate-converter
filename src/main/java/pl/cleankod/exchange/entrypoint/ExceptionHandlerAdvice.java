package pl.cleankod.exchange.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.entrypoint.model.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ValidationException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = {
            AccountNotFoundException.class
    })
    protected ResponseEntity<ApiError> handleAccountNotFoundException(AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(value = {
            ValidationException.class
    })
    protected ResponseEntity<ApiError> handleAccountNotFoundException(ValidationException exception) {
        return ResponseEntity.badRequest().body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

}
