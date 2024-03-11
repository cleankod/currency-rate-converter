package pl.cleankod.util.domain;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.util.nbp.exception.NbpApiBadRequestException;
import pl.cleankod.util.nbp.exception.NbpApiException;
import pl.cleankod.util.nbp.exception.NbpApiInternalServerException;
import pl.cleankod.util.nbp.exception.NbpApiNotFoundException;

public class NbpApiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
       return switch (response.status()) {
            case 400 -> new NbpApiBadRequestException("Bad request error occurred");
            case 404 -> new NbpApiNotFoundException("Resource not found error occurred");
            case 500 -> new NbpApiInternalServerException("Internal server error occurred");
            default -> new NbpApiException("Unknown error occurred");
        };
    }
}