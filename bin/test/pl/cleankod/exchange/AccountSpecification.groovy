package pl.cleankod.exchange

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.HttpResponse
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money

import java.nio.charset.StandardCharsets

class AccountSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()

        def body = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}"
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(WireMock.ok(body))
        )
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should return an account by ID"() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"

        when:
        Account response = get("/accounts/${accountId}", Account)

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
        Account response = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                Money.of("27.16", currency)
        )
    }

    def "should return an account by number"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        Account response = get("/accounts/number=${accountNumberUrlEncoded}", Account)

        then:
        response == new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of(accountNumberValue),
                Money.of("456.78", "EUR")
        )
    }

    def "should return an account by number with different currency"() {
        given:
        def accountNumberValue = "75 1240 2034 1111 0000 0306 8582"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)

        when:
        HttpResponse response = getResponse("/accounts/number=${accountNumberUrlEncoded}?currency=PLN")

        then:
        response.getStatusLine().getStatusCode() == 400
        transformError(response).message() == "Cannot convert currency from EUR to PLN."
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
