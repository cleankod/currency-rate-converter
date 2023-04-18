package pl.cleankod.exchange.entrypoint;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.exchange.provider.nbp.ExchangeRateNotAvailableException;

@Produces
@Singleton
@Requires(classes = {ExchangeRateNotAvailableException.class, ExceptionHandler.class})
public class ExchangeRateNotAvailableExceptionHandler implements ExceptionHandler<ExchangeRateNotAvailableException, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, ExchangeRateNotAvailableException exception) {
        return HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError(exception.getMessage()));
    }
}
