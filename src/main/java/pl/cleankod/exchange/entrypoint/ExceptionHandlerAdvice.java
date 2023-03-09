package pl.cleankod.exchange.entrypoint;

import feign.FeignException;
import feign.RetryableException;
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
    final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }


    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ApiError> handleDownstreamExceptions(FeignException e) {
        logger.error("There was an exception from NBP downstream ", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiError("There was a problem with currency rate supplier"));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> globalExceptionHandler(Exception e) {
        logger.error("Unhandled exception ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("We are working to bring back the service"));
    }


}
