package pl.cleankod.exchange

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.HttpResponse
import pl.cleankod.SystemUnderTest
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class AccountSpecification extends Specification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    private static sut = new SystemUnderTest()

    def setupSpec() {
        sut.startWithRandomPort()
        wireMockServer.start()

        def body = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}"
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR")
                        .willReturn(WireMock.ok(body))
        )
    }

    def cleanupSpec() {
        sut.stop()
        wireMockServer.stop()
    }

    def "should return an account by ID"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        Account response = sut.get("/accounts/${accountId}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("123.45", "PLN")
        )
    }

    def "should return an account by ID with different currency"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        Account response = sut.get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("27.16", currency)
        )
    }

    def "should not return an account by ID with non-existent currency"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "USB"

        when:
        HttpResponse response = sut.getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 400
        sut.transformError(response).message() == "Unknown currency: ${currency}."
    }

    def "should return an account by number"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        Account response = sut.get("/accounts/number=${accountNumberUrlEncoded}", Account)

        then:
        response == new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of(accountNumberValue),
                Money.of("456.78", "EUR")
        )
    }

    def "should return an account by number with different currency"() {
        given:
        def accountNumberValue = "65 1090 1665 0000 0001 0373 7343"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        Account response = sut.get("/accounts/number=${accountNumberUrlEncoded}?currency=EUR", Account)

        then:
        response == new Account(
                Account.Id.of("fa07c538-8ce4-11ec-9ad5-4f5a625cd744"),
                Account.Number.of(accountNumberValue),
                Money.of("27.16", "EUR")
        )
    }

    def "should not return an account by number with non-existent currency"() {
        given:
        def accountNumberValue = "65 1090 1665 0000 0001 0373 7343"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)
        def currency = "USB"

        when:
        HttpResponse response = sut.getResponse("/accounts/number=${accountNumberUrlEncoded}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 400
        sut.transformError(response).message() == "Unknown currency: ${currency}."
    }

    def "should return an error if account currency if different than base currency"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        HttpResponse response = sut.getResponse("/accounts/number=${accountNumberUrlEncoded}?currency=PLN")

        then:
        response.getStatusLine().getStatusCode() == 400
        sut.transformError(response).message() == "Cannot convert currency from EUR to PLN."
    }

    def "should not find an account by ID"() {
        given:
        def accountId = "ac270f3a-8d08-11ec-8b91-9bcdf6e2522a"

        when:
        def response = sut.getResponse("/accounts/${accountId}")

        then:
        response.getStatusLine().getStatusCode() == 404
    }

    def "should not find an account by number"() {
        given:
        def accountNumber = URLEncoder.encode("11 1750 0009 0000 0000 2156 6004", StandardCharsets.UTF_8)

        when:
        def response = sut.getResponse("/accounts/number=${accountNumber}")

        then:
        response.getStatusLine().getStatusCode() == 404
    }
}
