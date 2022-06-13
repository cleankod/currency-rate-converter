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

    def "will create MidRate"() {
        when:
        RateWrapper.MidRate rate = new RateWrapper.MidRate(BigDecimal.ONE, Currency.getInstance("PLN"));

        then:
        noExceptionThrown()
    }

    def "will create MidRate using Ratewapper"() {
        given:
        RateWrapper rateWrapper = new RateWrapper("Table", "pln", "PLN", [new Rate("1", ZonedDateTime.now().toString(), BigDecimal.ONE)])

        when:
        rateWrapper.midRate

        then:
        noExceptionThrown()
    }

    def "will throw exception for MidRate because rate is equal or lower then 0"() {
        when:
        RateWrapper.MidRate rate = new RateWrapper.MidRate(givenValue, Currency.getInstance("PLN"));

        then:
        thrown(ExternalClientParameterException)

        where:
        givenValue << [BigDecimal.ZERO, new BigDecimal(-100)]
    }

    def "will throw exception for RateWrapper because there are not rates"() {
        when:
        RateWrapper rateWrapper = new RateWrapper("Table", "pln", "PLN", [])

        then:
        thrown(ExternalClientParameterException)
    }

}
