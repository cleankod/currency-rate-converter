package pl.cleankod.util

import pl.cleankod.exchange.entrypoint.model.ValidationException
import spock.lang.Specification

class PreconditionsSpecification extends Specification {

    def "should return object since it's not null"() {
        when:
        //noinspection GroovyAssignabilityCheck
        def actualObject = Preconditions.requireNonNull(givenValue)

        then:
        actualObject == givenValue

        where:
        givenValue << ["Some value", 1L, Long.valueOf(1L)]
    }

    def "should not return object due to null value"() {
        when:
        //noinspection GroovyAssignabilityCheck
        Preconditions.requireNonNull(givenValue)

        then:
        def exception = thrown(NullPointerException)
        exception.message.startsWith("Given value cannot be null")

        where:
        givenValue << [null as String, null as Long]
    }

    def "should not validate account number because of wrong format"() {
        when:
        Preconditions.validateAccountNumber("x")

        then:
        def exception = thrown(ValidationException)
        exception.message.startsWith("The account number does not match the pattern")
    }

    def "should not validate id of an account because of wrong format"() {
        when:
        Preconditions.validateUUIDFormat("x")

        then:
        def exception = thrown(ValidationException)
        exception.message.startsWith("Format validation failed for id x")
    }

}
