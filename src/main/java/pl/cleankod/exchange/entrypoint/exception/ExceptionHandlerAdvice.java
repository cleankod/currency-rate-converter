package pl.cleankod.exchange.entrypoint.exception;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CurrencyRateConverterException.class)
    protected ResponseEntity<ErrorResponse> handleCurrencyRateConverterException(final CurrencyRateConverterException exception) {
        log.warn("handleCurrencyRateConverterException: errorMessage={}, exception", exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(exception.httpStatus).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException exception) {
        log.error("Unexpected exception has been thrown: errorMessage={}, exception", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(CurrencyRateConverterException.UNKNOWN_ERROR_MESSAGE));
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ErrorResponse> handleFeignClientException(final FeignException exception) {
        log.warn("Feign client returned exception: errorMessage={}, exception", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(CurrencyRateConverterException.EXTERNAL_SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    protected ResponseEntity<ErrorResponse> handleCircuitBreakerException(final CallNotPermittedException exception) {
        log.warn("CircuitBreaker for service <{}> kicked in", exception.getCausingCircuitBreakerName(), exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(CurrencyRateConverterException.EXTERNAL_SERVICE_UNAVAILABLE));
    }
}
