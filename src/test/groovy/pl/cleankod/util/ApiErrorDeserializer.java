package pl.cleankod.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import pl.cleankod.exchange.entrypoint.model.ApiError;

import java.io.IOException;


public class ApiErrorDeserializer extends JsonDeserializer<ApiError> {
    @Override
    public ApiError deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = jp.getCodec()
                .readTree(jp);
        final String message = node.get("message").textValue();
        return new ApiError(message);

    }


}
