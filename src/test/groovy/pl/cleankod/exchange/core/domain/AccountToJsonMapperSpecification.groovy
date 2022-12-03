package pl.cleankod.exchange.core.domain

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class AccountToJsonMapperSpecification extends Specification {
    def "should perform value-object serialization"() {

        given:
        ObjectMapper objectMapper = new ObjectMapper();
        Account account =  new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of("75 1240 2034 1111 0000 0306 8582"),
                Money.of("456.78", "EUR"))

        when:
        String accountAsJson = objectMapper.writeValueAsString(account);

        then:
        accountAsJson == "{\"id\":\"78743420-8ce9-11ec-b0d0-57b77255c208\",\"number\":\"75 1240 2034 1111 0000 0306 8582\",\"balance\":{\"amount\":456.78,\"currency\":\"EUR\"}}"
    }

    def "should perform value-object deserialization"() {

        given:
        ObjectMapper objectMapper = new ObjectMapper();
        Account account =  new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of("75 1240 2034 1111 0000 0306 8582"),
                Money.of("456.78", "EUR"))
        String accountAsJson = "{\"id\":\"78743420-8ce9-11ec-b0d0-57b77255c208\",\"number\":\"75 1240 2034 1111 0000 0306 8582\",\"balance\":{\"amount\":456.78,\"currency\":\"EUR\"}}"

        when:
        Account accountFromJson = objectMapper.readValue(accountAsJson, Account.class);

        then:
        accountFromJson == account;
    }
}
