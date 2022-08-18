package pl.cleankod.util.deserialize

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money

class AccountDeserializer extends JsonDeserializer<Account> {

    @Override
    Account deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p)

        def id = new Account.Id(UUID.fromString(node.get("id").asText()))
        def number = new Account.Number(node.get("number").asText())
        def balanceNode = node.get("balance")
        def amount = balanceNode.get("amount").asText()
        def cur = balanceNode.get("currency").asText()

        return new Account(id, number, Money.of(amount, cur))
    }
}
