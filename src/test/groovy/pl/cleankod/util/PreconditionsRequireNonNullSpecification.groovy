package pl.cleankod.util

import pl.cleankod.exchange.core.util.Preconditions
import spock.lang.Specification

class PreconditionsRequireNonNullSpecification extends Specification {
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
}
