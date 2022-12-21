package pl.cleankod.exchange.core.service

import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort
import pl.cleankod.exchange.util.Result
import spock.lang.Specification

class CurrencyConversionServiceSpecification extends Specification {

    def exchangeRatesProvider = Mock(ExchangeRatesProviderPort)
    def pln = Currency.getInstance("PLN")
    def currencyConversionService = new CurrencyConversionService(exchangeRatesProvider, pln)
    def eur = Currency.getInstance("EUR")

    def "should convert one PLN to equivalent EUR amount given by 1 over the exchange rate rounded half up"() {
        given:
        exchangeRatesProvider.getExchangeRate(eur) >> Result.successful(BigDecimal.valueOf(exchangeRate as double))

        when:
        def equivalentAmountInEurResult = currencyConversionService.convert(onePln as Money, eur)

        then:
        equivalentAmountInEurResult.amount().doubleValue() == correctlyRoundedAmount // 0.22d which is rounded 'half up' from 1/exchangeRate = 0.22087 | 0.21505

        where:
        exchangeRate       || correctlyRoundedAmount  || onePln
        4.5274d            || 0.22d                   || Money.of("1", "PLN")
        4.65d              || 0.22d                   || Money.of("1", "PLN")

    }
}
