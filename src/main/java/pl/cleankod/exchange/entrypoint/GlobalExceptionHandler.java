package pl.cleankod.exchange.entrypoint;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.entrypoint.model.ApiError;

public class GlobalExceptionHandler<T extends Throwable>
        implements ExceptionHandler<T, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, T exception) {
        return HttpResponse.badRequest(new ApiError(exception.getMessage()));
    }
}
