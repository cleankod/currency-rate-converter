package pl.cleankod.exchange.provider.nbp.model;

import feign.FeignException;
import org.springframework.http.HttpStatus;

public class ExternalClientParameterException extends FeignException {

    public ExternalClientParameterException(String reason) {
        super(HttpStatus.SERVICE_UNAVAILABLE.value(), reason);
    }
}
