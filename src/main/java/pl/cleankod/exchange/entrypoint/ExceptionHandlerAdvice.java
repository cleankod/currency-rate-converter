package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.exceptions.NbpAPIException;

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
    protected ResponseEntity<ApiError> handleIllegalArgumentExcheption(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError("Please check the provided arguments"));
    }

    @ExceptionHandler({
            NbpAPIException.class,
    })
    protected ResponseEntity<ApiError> handleNbpAPIException(NbpAPIException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({
            Exception.class
    })
    protected ResponseEntity<ApiError> handleIllegalArgumentExcheption(Exception ex) {
        return ResponseEntity.internalServerError().body(new ApiError("Technical Error. Please try again later"));
    }
}
