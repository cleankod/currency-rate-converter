package pl.cleankod.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.exchange.provider.nbp.exception.NbpApiRuntimeError;
import pl.cleankod.exchange.provider.nbp.exception.NbpBadRequestException;
import pl.cleankod.exchange.provider.nbp.exception.NbpInternalServerError;
import pl.cleankod.exchange.provider.nbp.exception.NbpServiceUnavailable;

public class NbpClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new NbpBadRequestException("Bad request to NBP API");
            case 500 -> new NbpInternalServerError("NBP Internal Server Error");
            case 503 -> new NbpServiceUnavailable("NBP Service Unavailable");
            default -> new NbpApiRuntimeError("Exception with NBP Service");
        };
    }
}
