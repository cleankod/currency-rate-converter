package pl.cleankod.exchange.entrypoint.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import feign.Request;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private Request.HttpMethod requestMethod;
    private String url;
    private String message;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String url, Request.HttpMethod requestMethod) {
        this();
        this.status = status;
        this.url = url;
        this.requestMethod = requestMethod;
    }

    public ApiError(String message) {
        this.message = message;
    }
}