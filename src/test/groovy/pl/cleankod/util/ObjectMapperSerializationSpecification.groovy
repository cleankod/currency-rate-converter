package pl.cleankod.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.ObjectMapper
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import spock.lang.Specification

class ObjectMapperSerializationSpecification extends Specification {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .addMixIn(Account.Id, IdMixin)
            .addMixIn(Account.Number, NumberMixin)

    def "deserialize entity with single value objects"() {
        given:
        def input = """{
                "balance": {
                    "amount": 27.27,
                    "currency": "EUR"
                },
                "id": "fa07c538-8ce4-11ec-9ad5-4f5a625cd744",
                "number": "65 1090 1665 0000 0001 0373 7343"
            }""".stripIndent()

        when:
        Account value = objectMapper.readValue(input, Account)

        then:
        value == new Account(Account.Id.of("fa07c538-8ce4-11ec-9ad5-4f5a625cd744"),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("27.27", "EUR"))
    }

    private static abstract class IdMixin {
        @JsonCreator
        static Account.Id of(String value) {
            return Account.Id.of(value)
        }
    }

    private static abstract class NumberMixin {
        @JsonCreator
        static Account.Number of(String value) {
            return Account.Number.of(value)
        }
    }
}
