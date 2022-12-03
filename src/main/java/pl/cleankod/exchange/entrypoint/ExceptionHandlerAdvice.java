package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.exception.BusinessException;
import pl.cleankod.exchange.core.exception.SystemException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<ApiError> handleBadRequest(Exception ex) {
        return ResponseEntity.internalServerError().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({ BusinessException.class })
    protected ResponseEntity<ApiError> handleBadRequest(BusinessException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    protected ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler({ SystemException.class })
    protected ResponseEntity<ApiError> handleBadRequest(SystemException ex) {
        return ResponseEntity.internalServerError().body(new ApiError(ex.getMessage()));
    }
}
