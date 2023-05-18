package pl.cleankod.util;

import org.springframework.http.ResponseEntity;
import pl.cleankod.exchange.core.domain.ApplicationError;
import pl.cleankod.exchange.entrypoint.model.ApiError;

public final class ErrorTransformer {

    private ErrorTransformer() {}

    public static ResponseEntity<ApiError> toApiError(ApplicationError applicationError) {
        return ResponseEntity
                .status(applicationError.getHttpStatus())
                .body(new ApiError(applicationError.getMessage()));
    }
}
