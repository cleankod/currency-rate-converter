package pl.cleankod.exchange.core.domain

import pl.cleankod.exchange.core.usecase.CurrencyConversionException
import spock.lang.Specification

import java.math.RoundingMode

class MoneyTest extends Specification {

    def "will create money"() {
        when:
        Money money = new Money(givenValue, Currency.getInstance("PLN"))

        then:
        noExceptionThrown()

        where:
        givenValue << [new BigDecimal(1), BigDecimal.ZERO]

    }

    def "will convert money"() {
        given:
        def currency = Currency.getInstance("PLN")
        def decimal = new BigDecimal(input).setScale(currency.defaultFractionDigits, RoundingMode.HALF_EVEN)
        Money money = new Money(decimal, currency)

        when:
        Money convertedMoney = money.convert(currency, Currency.getInstance("EUR"), new BigDecimal(rate))

        then:
        convertedMoney.amount() == new BigDecimal(output).setScale(currency.defaultFractionDigits, RoundingMode.HALF_EVEN)

        where:
        input   || rate || output
        1.00    || 1.00 || 1.00
        2.00    || 0.5  || 4.00
        2.00    || 2.0  || 1.00
        10.1111 || 0.5  || 20.22
    }

    def "will not convert money - base and target currency are the same"() {
        given:
        Money money = new Money(new BigDecimal(1.0), Currency.getInstance("EUR"))

        when:
        Money convertedMoney = money.convert(Currency.getInstance("PLN"), Currency.getInstance("PLN"), new BigDecimal(1.0))

        then:
        thrown(CurrencyConversionException)
    }

    def "will return the same object"() {
        given:
        Money money = new Money(new BigDecimal(1.0), Currency.getInstance("EUR"))

        when:
        Money convertedMoney = money.convert(Currency.getInstance("EUR"), Currency.getInstance("EUR"), new BigDecimal(1.0))

        then:
        convertedMoney.amount() == money.amount()
        convertedMoney == money
    }
}
