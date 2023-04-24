package pl.cleankod.exchange.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pl.cleankod.exchange.core.domain.Account;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        SimpleModule module = new SimpleModule("CustomModule", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Account.Id.class, new IdSerializer());
        module.addDeserializer(Account.Id.class, new IdDeserializer());
        module.addSerializer(Account.Number.class, new NumberSerializer());
        module.addDeserializer(Account.Number.class, new NumberDeserializer());
        registerModule(module);
    }

    private static class IdSerializer extends StdSerializer<Account.Id> {
        public IdSerializer() {
            super(Account.Id.class);
        }

        @Override
        public void serialize(Account.Id id, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(id.value().toString());
        }
    }

    private static class IdDeserializer extends StdDeserializer<Account.Id> {
        public IdDeserializer() {
            super(Account.Id.class);
        }

        @Override
        public Account.Id deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return Account.Id.of(p.getValueAsString());
        }
    }

    private static class NumberSerializer extends StdSerializer<Account.Number> {
        public NumberSerializer() {
            super(Account.Number.class);
        }

        @Override
        public void serialize(Account.Number number, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(number.value());
        }
    }

    private static class NumberDeserializer extends StdDeserializer<Account.Number> {
        public NumberDeserializer() {
            super(Account.Number.class);
        }

        @Override
        public Account.Number deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return Account.Number.of(p.getValueAsString());
        }
    }
}
