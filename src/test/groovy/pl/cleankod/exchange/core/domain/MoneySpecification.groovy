package pl.cleankod.exchange.core.domain


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

    def "should not create object with negative amount"() {
        when:
        Money.of(givenAmount, "PLN")

        then:
        thrown(IllegalArgumentException)

        where:
        givenAmount = "-1"
    }

    def "should create object with amount correctly rounded when division applied"() {
        when:
        Money money = Money.of(startingAmount, divideRate, Currency.getInstance("RON"))

        then:
        money != null
        money.amount() == expectedAmount

        where:
        startingAmount             | divideRate || expectedAmount
        BigDecimal.valueOf(0.01)   | 0.9152     || BigDecimal.valueOf(0.01)
        BigDecimal.valueOf(0.3)    | 0.9152     || BigDecimal.valueOf(0.33)
        BigDecimal.valueOf(0.5)    | 0.9152     || BigDecimal.valueOf(0.55)
        BigDecimal.valueOf(0.99)   | 0.9152     || BigDecimal.valueOf(1.08)
        BigDecimal.valueOf(1.0)    | 0.9152     || BigDecimal.valueOf(1.09)
        BigDecimal.valueOf(1.1)    | 0.9152     || BigDecimal.valueOf(1.20)
        BigDecimal.valueOf(1.11)   | 0.9152     || BigDecimal.valueOf(1.21)
        BigDecimal.valueOf(123.45) | 0.9152     || BigDecimal.valueOf(134.89)
        BigDecimal.valueOf(999.99) | 0.9152     || BigDecimal.valueOf(1092.65)

        BigDecimal.valueOf(0.01)   | 4.5274     || BigDecimal.valueOf(0.00)
        BigDecimal.valueOf(0.3)    | 4.5274     || BigDecimal.valueOf(0.07)
        BigDecimal.valueOf(0.5)    | 4.5274     || BigDecimal.valueOf(0.11)
        BigDecimal.valueOf(0.99)   | 4.5274     || BigDecimal.valueOf(0.22)
        BigDecimal.valueOf(1.0)    | 4.5274     || BigDecimal.valueOf(0.22)
        BigDecimal.valueOf(1.1)    | 4.5274     || BigDecimal.valueOf(0.24)
        BigDecimal.valueOf(1.11)   | 4.5274     || BigDecimal.valueOf(0.25)
        BigDecimal.valueOf(123.45) | 4.5274     || BigDecimal.valueOf(27.27)
        BigDecimal.valueOf(999.99) | 4.5274     || BigDecimal.valueOf(220.88)
    }
}
