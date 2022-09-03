package pl.cleankod.exchange.provider.nbp;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.provider.nbp.exception.NbpNotFountException;
import pl.cleankod.exchange.provider.nbp.exception.NbpServerErrorException;
import pl.cleankod.exchange.provider.nbp.exception.NbpSystemNotAvailableException;
import pl.cleankod.exchange.provider.nbp.exception.NbpUnauthorizedException;

public class NbpResponseDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NbpResponseDecoder.class);
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 401:
                logger.error("Unauthorized request for service");
                return new NbpUnauthorizedException("Unauthorized request for service");
            case 404:
                logger.error("Resource can not be found in service");
                return new NbpNotFountException("Resource can not be found in service");
            case 500:
                logger.error("Server was unable to process the request");
                return new NbpServerErrorException("Server was unable to process the request");
            case 503:
                logger.error("System unavailable");
                return new NbpSystemNotAvailableException("System unavailable");
        }

        return errorDecoder.decode(methodKey, response);
    }
}
