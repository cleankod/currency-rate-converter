package pl.cleankod.exchange.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;

import java.io.IOException;

public class AccountDeserializeService extends JsonDeserializer<Account> {
    @Override
    public Account deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode balanceNode = jsonNode.get("balance");

        return new Account(
                Account.Id.of(jsonNode.get("id").asText()),
                Account.Number.of(jsonNode.get("number").asText()),
                Money.of(balanceNode.get("amount").asText(), balanceNode.get("currency").asText())
        );
    }
}