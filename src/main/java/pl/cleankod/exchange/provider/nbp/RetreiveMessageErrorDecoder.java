package pl.cleankod.exchange.provider.nbp;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import static feign.FeignException.errorStatus;

public class RetreiveMessageErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = errorStatus(methodKey, response);
        return new FeignApiCustomException(exception.getMessage(),
                exception.status(),
                exception.request().httpMethod(),
                exception.request().url());
    }
}