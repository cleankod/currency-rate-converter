package pl.cleankod.prover.nbp

import com.google.common.collect.Lists
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.provider.CurrencyConversionNbpService
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClientWrapper
import pl.cleankod.exchange.provider.nbp.model.Rate
import pl.cleankod.exchange.provider.nbp.model.RateWrapper
import spock.lang.Specification

class CurrencyConversionNbpServiceSpecification extends Specification {

    ExchangeRatesNbpClientWrapper exchangeRatesNbpClient

    CurrencyConversionNbpService unit


    def setup() {
        exchangeRatesNbpClient = Mock(ExchangeRatesNbpClientWrapper.class)
        unit = new CurrencyConversionNbpService(exchangeRatesNbpClient)
    }

    def "Conversion rounding is correct"() {
        given:
        Money money = Money.of("1", "PLN")
        Currency currency = Currency.getInstance("EUR")
        Rate rate = new Rate("026/A/NBP/2022", "2022-02-08", 0.3)
        List<Rate> rates = Lists.asList(rate)
        RateWrapper rateWrapper = new RateWrapper("A","euro", "EUR", rates)
        exchangeRatesNbpClient.fetch("A", "EUR") >> rateWrapper

        when:
        Money result = unit.convert(money, currency)

        then:
        println result.amount().toString()
        result.amount().toString() == "3.33"
    }

}
