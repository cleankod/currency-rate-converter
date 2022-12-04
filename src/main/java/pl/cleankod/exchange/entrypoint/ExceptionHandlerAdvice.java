package pl.cleankod.exchange.entrypoint;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler({
            CurrencyConversionException.class,
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    protected ResponseEntity<ApiError> handleCallNotPermittedException(CallNotPermittedException ex) {
        LOG.warn("CircuitBreaker [{}] is open", ex.getCausingCircuitBreakerName(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("External provider temporarily not available."));
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ApiError> handleFeignClientException(FeignException ex) {
        LOG.warn("Feign exception", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("External service error."));

    }
}
