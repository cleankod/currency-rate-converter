package pl.cleankod.exchange

import org.apache.http.HttpResponse
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.dto.AccountDto

import java.nio.charset.StandardCharsets

class AccountSpecification extends BaseApplicationSpecification {

    def "should return an account by ID"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        AccountDto response = get("/accounts/${accountId}", AccountDto)

        then:
        response == new AccountDto(
                UUID.fromString(accountId),
                "65 1090 1665 0000 0001 0373 7343",
                response.balance()
        )
    }

    def "should return an account by ID with different currency"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        AccountDto response = get("/accounts/${accountId}?currency=${currency}", AccountDto)

        then:
        response == new AccountDto(
                UUID.fromString(accountId),
                "65 1090 1665 0000 0001 0373 7343",
                response.balance()
        )
    }

    def "should return an account by number"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        AccountDto response = get("/accounts/number=${accountNumberUrlEncoded}", AccountDto)

        then:
        response == new AccountDto(
                UUID.fromString("78743420-8ce9-11ec-b0d0-57b77255c208"),
                accountNumberValue,
                response.balance()
        )
    }

    def "should return that account number is not valid"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 85"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        HttpResponse response = getResponse("/accounts/number=${accountNumberUrlEncoded}")

        then:
        response.getStatusLine().getStatusCode() == 400
    }

    def "should return an account by number with different currency"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        HttpResponse response = getResponse("/accounts/number=${accountNumberUrlEncoded}?currency=PLN")

        then:
        response.getStatusLine().getStatusCode() == 400
    }

    def "should not find an account by ID"() {
        given:
        def accountId = "ac270f3a-8d08-11ec-8b91-9bcdf6e2522a"

        when:
        def response = getResponse("/accounts/${accountId}")

        then:
        response.getStatusLine().getStatusCode() == 404
    }

    def "should not find an account by number"() {
        given:
        def accountNumber = URLEncoder.encode("11 1750 0009 0000 0000 2156 6004", StandardCharsets.UTF_8)

        when:
        def response = getResponse("/accounts/number=${accountNumber}")

        then:
        response.getStatusLine().getStatusCode() == 404
    }
}
