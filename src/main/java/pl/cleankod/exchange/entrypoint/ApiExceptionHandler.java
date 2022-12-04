package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.FeignApiCustomException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            FeignApiCustomException.class
    })
    protected <T extends FeignApiCustomException> ResponseEntity<ApiError> handleFeignCustomException(T ex) {
        return buildResponseEntity(new ApiError(ex.getHttpStatus(), ex.getUrl(), ex.getRequestHttpMethod()));
    }

    @ExceptionHandler({
            CurrencyConversionException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ApiError> handleCurrencyConversionException(CurrencyConversionException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}