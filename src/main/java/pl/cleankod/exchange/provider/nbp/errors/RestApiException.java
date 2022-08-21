package pl.cleankod.exchange.provider.nbp.errors;

public class RestApiException extends Exception {
    private final String message;
    private final int status;
    public RestApiException(int status) {
        this.status = status;

        if(status >= 500) {
            this.message = "Server failed to fulfill a valid request due to an error with server";
        } else if (status >= 400) {
            this.message = "Client sent an invalid request";
        } else {
            this.message = "Unexpected internal server error";
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
