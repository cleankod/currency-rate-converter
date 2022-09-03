package pl.cleankod.exchange.entrypoint;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exception.AccountNotFoundException;
import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.core.exception.NotFoundException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.exception.NbpNotFountException;
import pl.cleankod.exchange.provider.nbp.exception.NbpServerErrorException;
import pl.cleankod.exchange.provider.nbp.exception.NbpSystemNotAvailableException;
import pl.cleankod.exchange.provider.nbp.exception.NbpUnauthorizedException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        if (Strings.isEmpty(ex.getMessage())) {
            return ResponseEntity.badRequest().body(new ApiError("Please check all the arguments in the request"));
        }

        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({AccountNotFoundException.class,
            NbpNotFountException.class})
    ResponseEntity<ApiError> handleNotFoundRequest(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(NbpUnauthorizedException.class)
    ResponseEntity<ApiError> handleUnauthorizedRequest(NbpUnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(NbpSystemNotAvailableException.class)
    ResponseEntity<ApiError> handleSystemNotAvailableRequest(NbpSystemNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(NbpServerErrorException.class)
    ResponseEntity<ApiError> handleServerErrorRequest(NbpServerErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(e.getMessage()));
    }

}
