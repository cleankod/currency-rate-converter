package pl.cleankod.exchange.provider.nbp

import org.springframework.beans.factory.annotation.Autowired
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.gateway.CurrencyConversionService

class ExchangeRatesNbpClientTest extends RealNbpSpecification {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    //I know that I shouldn't make parameterized test there, but I wanted to try it :D
    def "should return rate wrapper for currency"() {
        when:
        def result = currencyConversionService.convert(new Money(10.0.toBigDecimal(), Currency.getInstance(initCurrency)), Currency.getInstance(targetCurrency))

        then:
        result.amount() == expectedAmount
        result.currency().getCurrencyCode() == targetCurrency

        where:
        initCurrency || targetCurrency || expectedAmount
        "PLN"        || "EUR"          || 2.2
    }
}
