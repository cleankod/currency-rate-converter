package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.exceptions.ClientAuthorizationException;
import pl.cleankod.exchange.provider.nbp.exceptions.CurrencyNotFoundException;
import pl.cleankod.exchange.provider.nbp.exceptions.InvalidCurrencyRequestException;
import pl.cleankod.exchange.provider.nbp.exceptions.NbpServerException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	@ExceptionHandler({
			CurrencyConversionException.class,
			IllegalArgumentException.class
	})
	protected ResponseEntity<ApiError> handleBadRequest(CurrencyConversionException ex) {
		return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
	}

	@ExceptionHandler({
			CurrencyNotFoundException.class,
			InvalidCurrencyRequestException.class,
			ClientAuthorizationException.class,
			NbpServerException.class
	})
	protected ResponseEntity<ApiError> handleNbpExceptions(RuntimeException ex) {
		String genericMessage = "An error occurred while processing the request.";
		return ResponseEntity.internalServerError().body(new ApiError(genericMessage));
	}
}
