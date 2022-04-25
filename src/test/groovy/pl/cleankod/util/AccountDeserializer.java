package pl.cleankod.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;

import java.io.IOException;


public class AccountDeserializer extends JsonDeserializer<Account> {

    @Override
    public Account deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonNode node = jp.getCodec()
                .readTree(jp);
        final String id = node.get("id").textValue();
        final String number = node.get("number").textValue();
        final String currency = node.get("balance").get("currency").textValue();
        final String amount =  node.get("balance").get("amount").asText();
        return  new Account(Account.Id.of(id),
                Account.Number.of(number),
                Money.of(amount, currency));
    }


}
