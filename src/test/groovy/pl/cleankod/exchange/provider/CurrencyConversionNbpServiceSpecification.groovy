package pl.cleankod.exchange.provider

import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient
import pl.cleankod.exchange.provider.nbp.model.Rate
import pl.cleankod.exchange.provider.nbp.model.RateWrapper
import spock.lang.Specification

class CurrencyConversionNbpServiceSpecification extends Specification {
    def "should convert valid amount"() {
        when:
        ExchangeRatesNbpClient exchangeRatesNbpClient = Mock(ExchangeRatesNbpClient.class)
        exchangeRatesNbpClient.fetch(_ as String, _ as String) >> {
            return new RateWrapper("test", "USD", "USD", [new Rate(_ as String, _ as String, BigDecimal.valueOf(3))])
        }

        CurrencyConversionNbpService currencyConversionNbpService = new CurrencyConversionNbpService(
                exchangeRatesNbpClient
        )
        Money money = currencyConversionNbpService.convert(Money.of(givenAmount, givenCurrency), Currency.getInstance("USD"))

        then:
        money != null
        money.amount() == expectedAmount
        money.currency() == expectedCurrency

        where:
        givenAmount                    | givenCurrency               || expectedAmount                 || expectedCurrency
        "123.45"                       | "PLN"                       || BigDecimal.valueOf(41.15)     || Currency.getInstance("USD")
        "12345678.9"                   | "EUR"                       || BigDecimal.valueOf(4115226.30) || Currency.getInstance("USD")
    }
}
