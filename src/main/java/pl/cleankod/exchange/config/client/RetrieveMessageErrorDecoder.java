package pl.cleankod.exchange.config.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.exchange.exception.InvalidDataException;
import pl.cleankod.exchange.exception.ResourceNotFoundException;
import pl.cleankod.exchange.exception.error_type.ExchangeRateProviderErrorType;

public class RetrieveMessageErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(final String methodKey, final Response response) {
        return switch (response.status()) {
            case 400 -> new InvalidDataException(ExchangeRateProviderErrorType.INVALID_REQUEST,
                    "Exchange rate provider invalid request");
            case 404 -> new ResourceNotFoundException(ExchangeRateProviderErrorType.NOT_FOUND,
                    "Exchange rate provider resource not found");
            default -> errorDecoder.decode(methodKey, response);
        };
    }
}

