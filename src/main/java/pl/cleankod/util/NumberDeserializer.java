package pl.cleankod.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import pl.cleankod.exchange.core.domain.Account;

import java.io.IOException;

public class NumberDeserializer extends JsonDeserializer<Account.Number> {
    @Override
    public Account.Number deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String node = p.readValueAs(String.class);
        return new Account.Number(node);
    }
}
