package pl.cleankod.exchange.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.ApiException;
import pl.cleankod.exchange.entrypoint.model.IncorrectFormatException;
import pl.cleankod.exchange.entrypoint.model.CurrencyConversionException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiException> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiException(ex.getMessage()));
    }

    @ExceptionHandler(value = {
            AccountNotFoundException.class
    })
    protected ResponseEntity<ApiException> handleAccountNotFoundException(AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiException(exception.getMessage()));
    }

    @ExceptionHandler(value = {
            IncorrectFormatException.class
    })
    protected ResponseEntity<ApiException> handleAccountNotFoundException(IncorrectFormatException exception) {
        return ResponseEntity.badRequest().body(new ApiException(exception.getMessage()));
    }
}
