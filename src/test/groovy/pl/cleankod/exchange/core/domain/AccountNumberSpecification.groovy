package pl.cleankod.exchange.core.domain

import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.entrypoint.model.ValidationException
import spock.lang.Specification

class AccountNumberSpecification extends Specification {
    def "should create object for valid value"() {
        when:
        Account.Number accountNumber = Account.Number.of(givenValue)

        then:
        accountNumber != null

        where:
        givenValue << [
                "65 1090 1665 0000 0001 0373 7343",
                "651090 1665 0000 0001 0373 7343",
                "65 10901665 0000 0001 0373 7343",
                "65 1090 16650000 0001 0373 7343",
                "65 1090 1665 00000001 0373 7343",
                "65 1090 1665 0000 00010373 7343",
                "65 1090 1665 0000 0001 03737343",
                "65109016650000000103737343"
        ]
    }

    def "should not create object due to incorrect value"() {
        when:
        Account.Number.of(givenValue)

        then:
        def exception = thrown(expectedExceptionType)
        exception.message.startsWith(expectedExceptionMessage)

        where:
        givenValue                           || expectedExceptionType    || expectedExceptionMessage
        "PL65 1090 1665 0000 0001 0373 7343" || ValidationException || "The account number does not match the pattern"
        null                                 || NullPointerException     || "Given value cannot be null"
    }
}
