package pl.cleankod.util.domain;

import static pl.cleankod.util.domain.Failures.ACCOUNT_NOT_FOUND;
import static pl.cleankod.util.domain.Failures.CURRENCY_CONVERSION_EXCEPTION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.cleankod.exchange.entrypoint.model.ApiError;

public final class FailureToResponseMapper {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Unexpected error. Check logs for more details.";
    private static final Logger LOGGER = LoggerFactory.getLogger(FailureToResponseMapper.class);

    private FailureToResponseMapper() {
    }

    public static ResponseEntity mapFailureWithLogging(Failure errorInfo) {
        logError(errorInfo);
        return switch (errorInfo.code()) {
            case CURRENCY_CONVERSION_EXCEPTION -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                    errorInfo.message()));
            case ACCOUNT_NOT_FOUND -> ResponseEntity.notFound().build();
            default -> handleUnmappedErrorCode(errorInfo.code());
        };
    }

    private static void logError(Failure failure) {
        var message = failure.message();
        failure.exception()
                .ifPresentOrElse(x -> LOGGER.error(message, x), () -> LOGGER.error(message));

    }

    private static ResponseEntity handleUnmappedErrorCode(String errorCode) {
        LOGGER.error("The error code {} is not mapped", errorCode);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MESSAGE);
    }

}