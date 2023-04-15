package pl.cleankod.exchange.entrypoint;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.util.UnknownCurrencyException;

@Produces
@Singleton
@Requires(classes = {UnknownCurrencyException.class, ExceptionHandler.class})
public class UnknownCurrencyExceptionHandler implements ExceptionHandler<UnknownCurrencyException, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, UnknownCurrencyException exception) {
        return HttpResponse.badRequest(new ApiError(exception.getMessage()));
    }
}
