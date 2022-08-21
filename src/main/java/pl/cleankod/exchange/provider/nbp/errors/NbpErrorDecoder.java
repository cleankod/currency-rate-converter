package pl.cleankod.exchange.provider.nbp.errors;

import feign.Response;
import feign.codec.ErrorDecoder;

public class NbpErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400) {
            return new RestApiException(response.status());
        } else {
            return new Exception("Generic exception");
        }
    }
}
