package pl.cleankod.exchange.provider.nbp;

import feign.Request;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeignApiCustomException extends RuntimeException {

    Request.HttpMethod requestHttpMethod;
    String url;
    HttpStatus httpStatus;

    public FeignApiCustomException(String message, int httpStatus, Request.HttpMethod httpMethod, String url) {
        super(message);
        this.httpStatus = HttpStatus.resolve(httpStatus);
        this.requestHttpMethod = httpMethod;
        this.url = url;
    }
}