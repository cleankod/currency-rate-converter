package pl.cleankod.exchange.core.domain


import spock.lang.Specification

class AccountIdSpecification extends Specification {
    def "should create object for valid value"() {
        when:
        Account.Id accountId = Account.Id.of(givenValue)

        then:
        accountId != null

        where:
        givenValue << [
                "4e34421e-8cd9-11ec-a657-5fa089961eab",
                "6d89b1b2-8cd9-11ec-8b07-5fe17acd1df5",
                "72734bc0-8cd9-11ec-b6ac-93a008c2e057"
        ]
    }

    def "should not create object due to incorrect value"() {
        when:
        Account.Id.of(givenValue)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.startsWith("Invalid UUID string")

        where:
        givenValue << [
                "4e34421e8cd911eca6575fa089961eab",
                "a",
                "4e34421e8cd9-11ec-a657-5fa089961eab",
                "4e34421e-8cd911ec-a657-5fa089961eab",
                "4e34421e-8cd9-11eca657-5fa089961eab",
                "4e34421e-8cd9-11ec-a6575fa089961eab"
        ]
    }

    def "should not create object due to null String value"() {
        when:
        Account.Id.of(null as String)

        then:
        def exception = thrown(NullPointerException)
        exception.message.startsWith("Given value cannot be null")
    }

    def "should not create object due to null UUID value"() {
        when:
        Account.Id.of(null as UUID)

        then:
        def exception = thrown(NullPointerException)
        exception.message.startsWith("Given value cannot be null")
    }
}
