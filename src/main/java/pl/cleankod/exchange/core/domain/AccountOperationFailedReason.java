package pl.cleankod.exchange.core.domain;

import org.apache.http.HttpStatus;

public class AccountOperationFailedReason {

    private final String message;

    private final int statusCode;

    public AccountOperationFailedReason(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static AccountOperationFailedReason accountNotFound() {
        return new AccountOperationFailedReason("Account cannot be found", HttpStatus.SC_NOT_FOUND);
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
