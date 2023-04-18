package pl.cleankod.exchange.entrypoint;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@Produces
@Singleton
@Requires(classes = {CurrencyConversionException.class, ExceptionHandler.class})
public class CurrencyConversionExceptionHandler implements ExceptionHandler<CurrencyConversionException, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, CurrencyConversionException exception) {
        return HttpResponse.badRequest(new ApiError(exception.getMessage()));
    }
}
