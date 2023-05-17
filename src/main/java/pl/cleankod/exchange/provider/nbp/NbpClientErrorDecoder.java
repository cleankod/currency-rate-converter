package pl.cleankod.exchange.provider.nbp;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.exchange.provider.nbp.exception.NbpClientException;

public class NbpClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new NbpClientException("Bad request to the NBP API.");
            case 404 -> new NbpClientException("NBP API cannot be found.");
            case 503 -> new NbpClientException("NBP API is unavailable.");
            default -> new NbpClientException("NBP API request failed.");
        };
    }
}
