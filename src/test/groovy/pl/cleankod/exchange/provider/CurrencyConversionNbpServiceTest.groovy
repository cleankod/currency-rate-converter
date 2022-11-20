package pl.cleankod.exchange.provider

import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient
import pl.cleankod.exchange.provider.nbp.model.Rate
import pl.cleankod.exchange.provider.nbp.model.RateWrapper
import spock.lang.Specification

class CurrencyConversionNbpServiceTest extends Specification {
    def "should convert amount using scale"() {
        given:
        ExchangeRatesNbpClient exchangeRatesNbpClient = Mock(ExchangeRatesNbpClient.class)
        exchangeRatesNbpClient.fetch(_ as String, _ as String) >> {
            return new RateWrapper("rate", "EUR", "EUR", [new Rate(_ as String, _ as String, BigDecimal.valueOf(4.5274))])
        }

        CurrencyConversionNbpService currencyConversionNbpService = new CurrencyConversionNbpService(exchangeRatesNbpClient)

        when:
        Money money = currencyConversionNbpService.convert(Money.of(givenAmount, givenCurrency), Currency.getInstance("EUR"))

        then:
        money != null
        money.amount() == expectedAmount
        money.currency() == expectedCurrency

        where:
        givenAmount               | givenCurrency               || expectedAmount               || expectedCurrency
        "1"                       | "PLN"                       || BigDecimal.valueOf(0.22)     || Currency.getInstance("EUR")
    }
}
