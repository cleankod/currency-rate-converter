package pl.cleankod.exchange.provider.nbp.model

import spock.lang.Specification

import java.time.ZonedDateTime

class RateWrapperTest extends Specification {

    def "will create RateWrapper"() {
        when:
        RateWrapper rateWrapper = new RateWrapper("Table", "pln", "PLN", [new Rate("1", ZonedDateTime.now().toString(), BigDecimal.ONE)])

        then:
        noExceptionThrown()
    }

    def "will throw exception for RateWrapper because there are not rates"() {
        when:
        RateWrapper rateWrapper = new RateWrapper("Table", "pln", "PLN", [])

        then:
        thrown(ExternalClientParameterException)
    }

}
