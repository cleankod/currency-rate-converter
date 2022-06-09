package pl.cleankod.exchange.provider.nbp

import org.springframework.beans.factory.annotation.Autowired
import pl.cleankod.exchange.core.gateway.CurrencyConversionService

class ExchangeRatesNbpClientTest extends RealNbpSpecification {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    //I know that I shouldn't make parameterized test there, but I wanted to try it :D
    def "should return rate wrapper for currency"() {
        when:
        def result = currencyConversionService.convert(Currency.getInstance(targetCurrency))

        then:
        result.rate() == rate

        where:
        targetCurrency || rate
        "EUR"          || 4.5274
        "GBP"          || 5.3792
    }
}
