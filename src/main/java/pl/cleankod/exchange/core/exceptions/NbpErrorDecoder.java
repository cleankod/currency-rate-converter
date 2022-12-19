package pl.cleankod.exchange.core.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;

public class NbpErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new BadRequestException("NBP API returned bad request");
            case 404 -> new NotFoundException("NBP API returned not found");
            default -> new Exception("Unexpected NBP API error occurred");
        };
    }
}
