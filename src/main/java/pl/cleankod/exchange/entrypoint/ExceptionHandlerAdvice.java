package pl.cleankod.exchange.entrypoint;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.service.AccountNotFoundException;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.FetchFallbackInvokedException;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOGGER= LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler({AccountNotFoundException.class})
    protected ResponseEntity<ApiError> handleAccountNotFoundException(AccountNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({FetchFallbackInvokedException.class})
    protected ResponseEntity<ApiError> handleFetchFallbackInvokedException(FetchFallbackInvokedException ex) {
        if (ex.getCause() instanceof CallNotPermittedException) {
            LOGGER.info("Nbp circuit breaker open:", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("Nbp client unavailable"));
        } else if (ex.getCause() instanceof FeignException feignException) {
            return ResponseEntity.status(feignException.status()).body(new ApiError(HttpStatus.valueOf(feignException.status()).getReasonPhrase()));
        } else {
            return ResponseEntity.internalServerError().body(new ApiError("Unexpected Exception thrown"));
        }
    }

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        LOGGER.info("Bad request:", ex);
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }
}
