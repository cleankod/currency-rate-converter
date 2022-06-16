package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.exceptions.BaseException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class,
            BaseException.class
    })

    protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage(), BaseException.ErrorCode.GENERAL));
    }

    protected ResponseEntity<ApiError> handleBaseException(BaseException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ApiError(ex.getMessage(), ex.getCode()));
    }
}
