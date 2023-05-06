package pl.cleankod.exchange.core.domain

import spock.lang.Specification

class ExchangeRateSpecification extends Specification {
    def "should return correct inverse exchange rate"() {
        given:
        ExchangeRate exchangeRate = ExchangeRate.of(source, target, rate)

        when:
        ExchangeRate inverseExchangeRate = exchangeRate.inverse()

        then:
        inverseExchangeRate.source == Currency.getInstance(target)
        inverseExchangeRate.target == Currency.getInstance(source)
        inverseExchangeRate.rate.toString() == expectedRate

        where:
        source | target | rate  | expectedRate
        "USD"  | "EUR"  | "0.5" | "2.000000"
        "USD"  | "PLN"  | "3"   | "0.333333"
    }
}
