package pl.cleankod.exchange.provider

import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.core.exceptions.RateRetrievalException
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient
import pl.cleankod.exchange.provider.nbp.model.Rate
import pl.cleankod.exchange.provider.nbp.model.RateWrapper
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class CurrencyConversionNbpServiceSpec extends Specification {
    private static def MONEY = Money.of('123.45', 'PLN')
    private static def EUR_CURRENCY = Currency.getInstance('EUR')

    private def exchangeRatesNbpClient = Mock(ExchangeRatesNbpClient.class)

    private CurrencyConversionNbpService createService(String currencyExpirationDuration = null,
                                                       String currencyExpirationTimeUnit = null) {
        new CurrencyConversionNbpService(exchangeRatesNbpClient, currencyExpirationDuration, currencyExpirationTimeUnit)
    }

    def 'should convert money to new currency'() {
        given:
        exchangeRatesNbpClient.fetch(_, _) >> createRateWrapper(rate)
        def service = createService()

        when:
        Money result = service.convert(Money.of(amount, 'PLN'), EUR_CURRENCY)

        then:
        result.amount() == new BigDecimal(expectedAmount)
        result.currency() == EUR_CURRENCY

        where:
        amount       | rate    || expectedAmount
        '123.45'     | 4.5452d || '27.16'
        '125.45'     | 4.5452d || '27.6'
        '125.4'      | 4.5452d || '27.59'
        '125.400'    | 4.5452d || '27.59'
        '1256789.44' | 4.5329d || '277259.47'
    }

    def 'should cache result of fetched rates'() {
        given:
        def rateWrapper1 = createRateWrapper(10d)
        def rateWrapper2 = createRateWrapper(4.5452d)
        exchangeRatesNbpClient.fetch(_, _) >>> [rateWrapper1, rateWrapper2]

        def service = createService(currencyExpirationDuration, currencyExpirationTimeUnit)

        when:
        Money result = service.convert(MONEY, EUR_CURRENCY)

        then:
        result.amount() == BigDecimal.valueOf(12.34d)

        when:
        Money result2 = service.convert(MONEY, EUR_CURRENCY)

        then:
        result2.amount() == BigDecimal.valueOf(expectedAmount)

        where:
        currencyExpirationDuration | currencyExpirationTimeUnit   || expectedAmount
        null                       | null                         || 12.34d
        "0"                        | TimeUnit.MICROSECONDS.name() || 27.16d
    }

    def 'should propagate rate exception'() {
        given:
        exchangeRatesNbpClient.fetch(_, _) >> new RuntimeException("exception message")

        def service = createService()

        when:
        service.convert(MONEY, EUR_CURRENCY)

        then:
        def exception = thrown(RateRetrievalException.class)
        exception.message == "Could not fetch rates for currency EUR"

    }

    def 'should throw exception for null rate'() {
        given:
        exchangeRatesNbpClient.fetch(_, _) >> null

        def service = createService()

        when:
        service.convert(MONEY, EUR_CURRENCY)

        then:
        def exception = thrown(RateRetrievalException.class)
        exception.message == "Could not retrieve rate for currency EUR"
    }

    private def createRateWrapper(double value) {
        return new RateWrapper('table', 'EUR', 'code',
                List.of(new Rate('', '', BigDecimal.valueOf(value))))
    }
}
