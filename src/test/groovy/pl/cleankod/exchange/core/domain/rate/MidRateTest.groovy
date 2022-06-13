package pl.cleankod.exchange.core.domain.rate

import spock.lang.Specification

class MidRateTest extends Specification {

    def "will create mid rate successfully"() {
        when:
        MidRate rate = new MidRate(Currency.getInstance("PLN"), BigDecimal.ONE)

        then:
        noExceptionThrown()
    }

    def "will throw exception for invalid mid rate"() {
        when:
        MidRate rate = new MidRate(Currency.getInstance("PLN"), givenValue)

        then:
        def exception = thrown(MidRateCanNotBeLowerThenZeroException)
        exception.message == "Mid rate can not be less then 0!"

        where:
        givenValue << [BigDecimal.ZERO, new BigDecimal(-1)]
    }
}
