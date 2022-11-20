package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.entrypoint.model.NbpApiException;
import pl.cleankod.exchange.entrypoint.model.NbpExceptionMessage;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(NbpApiException.class)
    protected ResponseEntity<NbpExceptionMessage> handleNbpApiError(NbpApiException ex) {
        NbpExceptionMessage message = ex.getNbpExceptionMessage();
        return ResponseEntity.status(message.getStatus()).body(message);
    }
}
