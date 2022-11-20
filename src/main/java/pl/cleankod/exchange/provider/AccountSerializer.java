package pl.cleankod.exchange.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import pl.cleankod.exchange.core.domain.Account;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(
            Account account, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("id", account.id().value().toString());
        jgen.writeStringField("number", account.number().value());
        jgen.writePOJOField("balance", account.balance());
        jgen.writeEndObject();
    }
}