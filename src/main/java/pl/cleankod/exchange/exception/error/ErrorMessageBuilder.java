package pl.cleankod.exchange.exception.error;

import org.springframework.http.HttpStatus;
import pl.cleankod.exchange.exception.error_type.ErrorType;

import java.time.Instant;

public final class ErrorMessageBuilder {

    public static ErrorMessage buildExceptionMessage(final ErrorType errorType, final String developerMessage,
                                                     final HttpStatus httpStatus) {
        return new ErrorMessage(httpStatus.value(), httpStatus.getReasonPhrase(), Instant.now(),
                toError(errorType, developerMessage));
    }

    private static Error toError(final ErrorType errorType, final String developerMessage) {
        return new Error(
                errorType == null ? null : errorType.getCode(),
                errorType == null ? null : errorType.getDescription(),
                developerMessage);
    }

}
