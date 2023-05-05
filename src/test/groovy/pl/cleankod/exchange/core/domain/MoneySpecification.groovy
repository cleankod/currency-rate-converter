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

    def "should create correct Money instance based on amount and currency"() {
        when:
        Money money = Money.of(amount, currency)

        then:
        money.class == expectedClass

        where:
        amount     | currency | expectedClass
        "100"      | "USD"    | WholeMoney
        "100.5"    | "USD"    | WholeMoney
        "100.60"   | "USD"    | WholeMoney
        "100.700"  | "USD"    | WholeMoney
        "100.111"  | "USD"    | FractionalMoney
        "100"      | "JPY"    | WholeMoney
        "100.5"    | "JPY"    | FractionalMoney
        "100.50"   | "JPY"    | FractionalMoney
        "100.111"  | "BHD"    | WholeMoney
        "100.1111" | "BHD"    | FractionalMoney
    }

    def "should create WholeMoney with scaled number of fractional digits"() {
        when:
        int numberOfFractionsDigits = Currency.getInstance(currency).defaultFractionDigits
        WholeMoney money = WholeMoney.of(amount, currency)

        then:
        money.amount().scale() == numberOfFractionsDigits

        where:
        amount     | currency
        "1"        | "USD"
        "1.1"      | "USD"
        "1.11"     | "USD"
        "1.1100"   | "USD"
        "10"       | "JPY"
        "10.000"   | "JPY"
        "10"       | "BHD"
        "10.0"     | "BHD"
        "10.00000" | "BHD"
    }

    def "should not create WholeMoney when amount is not whole for given currency"() {
        when:
        WholeMoney.of(amount, currency)

        then:
        thrown(IllegalArgumentException)

        where:
        amount    | currency
        "1.001"   | "USD"
        "10.1"    | "JPY"
        "10.01"   | "JPY"
        "10.0001" | "BHD"
    }

    def "should not create FractionalMoney when amount is whole for given currency"() {
        when:
        FractionalMoney.of(amount, currency)

        then:
        thrown(IllegalArgumentException)

        where:
        amount    | currency
        "10.01"   | "USD"
        "10.010"  | "USD"
        "10"      | "JPY"
        "10.00"   | "JPY"
        "10.001"  | "BHD"
        "10.0010" | "BHD"
    }
}
