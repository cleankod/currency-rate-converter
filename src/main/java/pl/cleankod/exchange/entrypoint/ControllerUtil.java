package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.util.domain.AppErrors;
import spark.Response;

import java.util.Currency;
import java.util.Optional;

public class ControllerUtil {

    public static <E extends AppErrors.Base> ApiError processError(Response response, E error) {
        if (error instanceof AppErrors.Conversion) {
            return badRequest(response, error.getMessage());
        } else if (error instanceof AppErrors.IllegalArgument) {
            return badRequest(response, error.getMessage());
        } else if (error instanceof AppErrors.NotFound) {
            return notFound(response, error.getMessage());
        } else if (error instanceof AppErrors.Provider) {
            return internal(response, error.getMessage());
        } else {
            return internal(response);
        }
    }

    public static <T> T ok(Response response, T body) {
        response.status(200);
        response.type("application/json");
        return body;
    }

    public static ApiError badRequest(Response response, String message) {
        response.status(400);
        response.type("application/json");
        return new ApiError(message);
    }

    public static ApiError notFound(Response response, String message) {
        response.status(404);
        response.type("application/json");
        return new ApiError(message);
    }

    public static ApiError internal(Response response) {
        return internal(response, "Internal server error");
    }

    public static ApiError internal(Response response, String message) {
        response.status(500);
        response.type("application/json");
        return new ApiError(message);
    }

    public static Currency strToCurrency(String currency) {
        return Optional.ofNullable(currency)
                .map(Currency::getInstance)
                .orElse(null);
    }

    public static String asString(ObjectMapper objectMapper, ApiError apiError) {
        try {
            return objectMapper.writeValueAsString(apiError);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }


}
