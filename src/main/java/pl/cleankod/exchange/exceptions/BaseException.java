package pl.cleankod.exchange.exceptions;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {

    private final ErrorCode code;
    private final HttpStatus httpStatus;

    public enum ErrorCode {
        GENERAL("1"),
        THIRD_PARTY_CLIENT("2");

        private final String errorCode;

        ErrorCode(String code) {
            this.errorCode = code;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

    public BaseException(String message, ErrorCode code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BaseException(String message, ErrorCode code, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public ErrorCode getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
