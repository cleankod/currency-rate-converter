package pl.cleankod.exchange.entrypoint.model;

public class NbpExceptionMessage {
    private int status;
    private String error;
    private String message;

    public NbpExceptionMessage() {
    }

    public NbpExceptionMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}