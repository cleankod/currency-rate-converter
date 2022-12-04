package pl.cleankod;

import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.core.exception.UnknownCurrencyException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.exception.NbpClientException;

//@ControllerAdvice - TODO find quasar alternative OR use `Result` (`either`) approach everywhere
public class ExceptionHandlerAdvice {

//    @ExceptionHandler(CurrencyConversionException.class)
//    protected ResponseEntity<ApiError> handleCurrencyConversionException(CurrencyConversionException ex) {
//        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
//    }
//
//    @ExceptionHandler(UnknownCurrencyException.class)
//    protected ResponseEntity<ApiError> handleUnknownCurrencyException(UnknownCurrencyException ex) {
//        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
//    }
//
//    @ExceptionHandler(NbpClientException.class)
//    protected ResponseEntity<ApiError> handleNbpClientException(NbpClientException ex) {
//        return ResponseEntity.internalServerError().body(new ApiError(ex.getMessage()));
//    }
}
