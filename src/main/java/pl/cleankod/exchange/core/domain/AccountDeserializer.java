package pl.cleankod.exchange.core.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class AccountDeserializer extends JsonDeserializer<Account> {

    @Override
    public Account deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        //possible more validation if field exists, but well
        JsonNode node = jp.getCodec().readTree(jp);
        String id = node.get("id").asText();
        String number = node.get("number").asText();
        String amount = node.get("balance").get("amount").asText();
        String currency = node.get("balance").get("currency").asText();
        return new Account(Account.Id.of(id), Account.Number.of(number), Money.of(amount, currency));
    }
}