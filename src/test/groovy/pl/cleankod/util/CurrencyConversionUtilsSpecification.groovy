package pl.cleankod.util


import spock.lang.Specification

import java.time.LocalDate

class CurrencyConversionUtilsSpecification extends Specification {

    def "should return 15.07.2022 (Friday) when getting data on 16.07.2022 (Saturday)"() {
        when:
        def localDate = CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.of(2022, 07, 16))

        then:
        localDate == LocalDate.of(2022, 07, 15)

    }

    def "should return 15.07.2022 (Friday) when getting data on 17.07.2022 (Sunday)"() {
        when:
        def localDate = CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.of(2022, 07, 17))

        then:
        localDate == LocalDate.of(2022, 07, 15)

    }

    def "should return 15.07.2022 (Friday) when getting data on 18.07.2022 (Monday)"() {
        when:
        def localDate = CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.of(2022, 07, 18))

        then:
        localDate == LocalDate.of(2022, 07, 15)
    }

    def "should return 18.07.2022(Monday) when getting data on Tuesday"() {
        when:
        def localDate = CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.of(2022, 07, 19))

        then:
        localDate == LocalDate.of(2022, 07, 18)
    }

}
