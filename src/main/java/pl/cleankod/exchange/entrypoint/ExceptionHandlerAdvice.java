package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }
}
