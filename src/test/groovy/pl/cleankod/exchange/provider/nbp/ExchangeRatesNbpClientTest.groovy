package pl.cleankod.exchange.provider.nbp

import org.springframework.beans.factory.annotation.Autowired
import pl.cleankod.exchange.core.gateway.CurrencyConversionService

class ExchangeRatesNbpClientTest extends RealNbpSpecification {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    def "should return rate wrapper for currency"() {
        when:
        def result = currencyConversionService.getMidRate(Currency.getInstance(targetCurrency), Currency.getInstance("PLN"))

        then:
        result.rate() == rate

        where:
        targetCurrency || rate
        "EUR"          || 4.5274
        "GBP"          || 5.3792
        "PLN" || 1
    }
}
