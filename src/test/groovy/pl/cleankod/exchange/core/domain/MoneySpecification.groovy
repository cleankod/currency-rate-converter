package pl.cleankod.exchange.core.domain


import pl.cleankod.exchange.core.domain.Money
import spock.lang.Specification

class MoneySpecification extends Specification {
    def "should create object for valid values"() {
        when:
        Money money = Money.of(givenAmount, givenCurrency)

        then:
        money != null
        money.amount() == expectedAmount
        money.currency() == expectedCurrency

        where:
        givenAmount                    | givenCurrency               || expectedAmount                 || expectedCurrency
        "123.45"                       | "PLN"                       || BigDecimal.valueOf(123.45)     || Currency.getInstance("PLN")
        "12345678.9"                   | "EUR"                       || BigDecimal.valueOf(12345678.9) || Currency.getInstance("EUR")
        BigDecimal.valueOf(12345678.9) | Currency.getInstance("EUR") || BigDecimal.valueOf(12345678.9) || Currency.getInstance("EUR")
    }

    def "should not create object due to null value"() {
        when:
        Money.of(givenAmount, givenCurrency)

        then:
        def exception = thrown(NullPointerException)
        exception.message.startsWith("Given value cannot be null")

        where:
        givenAmount | givenCurrency
        null        | null
        null        | "PLN"
        "123.45"    | null
    }

    def "should not create object due to incorrect currency value"() {
        when:
        Money.of("123.45", givenCurrency)

        then:
        thrown(IllegalArgumentException)

        where:
        givenCurrency << ["ZZZ", "123.45", "---", "000", "A"]
    }

    def "should not create object due to incorrect amount value"() {
        when:
        Money.of(givenAmount, "EUR")

        then:
        thrown(IllegalArgumentException)

        where:
        givenAmount << ["EUR", "\0", "123,45"]
    }

    def "should convert the amount correctly based on conversion rate"() {
        when:
        Money converted = money.convert(conversionRate, targetCurrency)

        then:
        converted != null
        converted.amount().scale() == targetCurrency.getDefaultFractionDigits()
        converted.amount() == expectedAmount

        where:
        money                     | conversionRate | targetCurrency              || expectedAmount
        Money.of("0", "PLN")      | 4.5287         | Currency.getInstance("EUR") || 0
        Money.of("0.1", "PLN")    | 4.5287         | Currency.getInstance("EUR") || 0.02
        Money.of("1.23", "PLN")   | 4.5287         | Currency.getInstance("EUR") || 0.27
        Money.of("10", "PLN")     | 4.5287         | Currency.getInstance("EUR") || 2.21
        Money.of("123.45", "PLN") | 4.5287         | Currency.getInstance("EUR") || 27.26

        Money.of("0", "EUR")      | 0.2207         | Currency.getInstance("PLN") || 0
        Money.of("10", "EUR")     | 0.2207         | Currency.getInstance("PLN") || 45.31
        Money.of("100", "EUR")    | 0.2207         | Currency.getInstance("PLN") || 453.10
        Money.of("123.45", "EUR") | 0.2207         | Currency.getInstance("PLN") || 559.36
    }
}
