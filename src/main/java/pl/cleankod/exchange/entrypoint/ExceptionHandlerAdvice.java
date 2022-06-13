package pl.cleankod.exchange.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        if (ex.getMessage() != null) {
            return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
        }

        return ResponseEntity.badRequest().body(new ApiError("Invalid input."));
    }

    @ExceptionHandler({
            ConstraintViolationException.class
    })
    protected ResponseEntity<ApiError> handleBadConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }
}
