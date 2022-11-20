package pl.cleankod.exchange.provider.nbp;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import pl.cleankod.exchange.entrypoint.model.NbpExceptionMessage;
import pl.cleankod.exchange.entrypoint.model.NbpApiException;

import java.io.IOException;
import java.io.InputStream;

public class NbpApiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        NbpExceptionMessage message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, NbpExceptionMessage.class);
        } catch (IOException e) {
            return new NbpApiException(new NbpExceptionMessage(response.status(), response.reason()));
        }
        return new NbpApiException(message);
    }
}
