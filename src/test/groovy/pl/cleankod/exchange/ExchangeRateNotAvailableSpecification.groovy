package pl.cleankod.exchange

import pl.cleankod.SystemUnderTest
import spock.lang.Specification

class ExchangeRateNotAvailableSpecification extends Specification {
    private static sut = new SystemUnderTest()

    def setupSpec() {
        sut.startWithRandomPort()
    }

    def "should return 503 if currency conversion is needed and there is no rate available for use. "() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        def response = sut.getResponse("/accounts/${accountId}?currency=EUR")

        then:
        response.getStatusLine().getStatusCode() == 503
    }

}
