package pl.cleankod.exchange.entrypoint;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.exception.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.exception.NbpClientErrorException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            CurrencyConversionException.class,
            NbpClientErrorException.class,
            IllegalStateException.class
    })
    protected ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    protected ResponseEntity<ApiError> handleCallNotPermittedException(CallNotPermittedException ex) {
        return ResponseEntity.badRequest().body(new ApiError("External calls failure rate too high."));
    }

}
