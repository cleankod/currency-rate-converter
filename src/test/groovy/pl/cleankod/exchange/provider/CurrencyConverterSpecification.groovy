package pl.cleankod.exchange.provider

import pl.cleankod.exchange.core.domain.Money
import spock.lang.Specification


class CurrencyConverterSpecification extends Specification {
    private static Currency USD = Currency.getInstance("USD")
    private static Currency EUR = Currency.getInstance("EUR")

    def "Currency Conversion should not lose money"() {
        when:
        CurrencyConverter currencyConverter = new CurrencyConverter()
        Money result = currencyConverter.convert(money, targetCurrency, exchangeRate)

        then:
        result == expectedResult

        where:
        money                     | targetCurrency | exchangeRate                || expectedResult
        Money.of("123", "PLN")    | USD            | BigDecimal.valueOf(4.42d)   || Money.of("27.83", "USD")
        Money.of("123.1", "PLN")  | EUR            | BigDecimal.valueOf(4.52d)   || Money.of("27.23", "EUR")
        Money.of("123.45", "PLN") | EUR            | BigDecimal.valueOf(4.5452d) || Money.of("27.16", "EUR")
    }
}
