package pl.cleankod.util;

import spock.lang.Specification

import java.math.RoundingMode;

class MoneyRoundingUtilSpecification extends Specification {

    def "should provide expected values after rounding"() {
        when:
        def actualObject = MoneyRoundingUtil.roundHalfEven(givenValue)

        then:
        actualObject == expectedValue

        where:
        givenValue << [new BigDecimal(0.004501d).setScale(6, RoundingMode.HALF_UP),
                       new BigDecimal(0.005001d).setScale(6, RoundingMode.HALF_UP),
                       new BigDecimal(0.005401d).setScale(6, RoundingMode.HALF_UP),
                       new BigDecimal(0.005501d).setScale(6, RoundingMode.HALF_UP),
                       new BigDecimal(0.005601d).setScale(6, RoundingMode.HALF_UP)]
        expectedValue << [new BigDecimal("0.00"),
                          new BigDecimal("0.00"),
                          new BigDecimal("0.00"),
                          new BigDecimal("0.01"),
                          new BigDecimal("0.01")]
    }

    def "should not return object due to null value"() {
        when:
        MoneyRoundingUtil.roundHalfEven(null)

        then:
        def exception = thrown(NullPointerException)
        exception.message.startsWith("Given value cannot be null")

    }


}