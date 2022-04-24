package pl.cleankod.exchange

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.Fault
import org.apache.http.HttpResponse
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money

import java.nio.charset.StandardCharsets

import static com.github.tomakehurst.wiremock.client.WireMock.*

class AccountSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )
    public static final String PLN_ACCOUNT_ID = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
    public static final String PLN_ACCOUNT_NUMBER = "65 1090 1665 0000 0001 0373 7343"
    public static final String EUR_ACCOUNT_NUMBER = "75 1240 2034 1111 0000 0306 8582"

    def setupSpec() {
        wireMockServer.start()

        def body = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}"
        wireMockServer.stubFor(
                get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(ok(body))
        )
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should return an account by ID"() {
        given:
        def accountId = PLN_ACCOUNT_ID

        when:
        Account response = get("/accounts/${accountId}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of(PLN_ACCOUNT_NUMBER),
                Money.of("123.45", "PLN")
        )
    }

    def "should return an account by ID with different currency"() {
        given:
        def accountId = PLN_ACCOUNT_ID
        def currency = "EUR"

        when:
        Account response = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of(PLN_ACCOUNT_NUMBER),
                Money.of("27.16", currency)
        )
    }

    def "should return a readable message if NBP service is not available"() {
        given:
        wireMockServer.stubFor(
                get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
        )
        def accountId = PLN_ACCOUNT_ID
        def currency = "EUR"

        when:
        Account response = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        wireMockServer.verify(getRequestedFor(urlEqualTo("/exchangerates/rates/A/EUR/2022-02-08")))
        response == new Account(
                Account.Id.of(accountId),
                Account.Number.of(PLN_ACCOUNT_NUMBER)
        )
    }

    def "should return an account by number"() {
        given:
        def accountNumberValue = EUR_ACCOUNT_NUMBER
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
        def accountNumberValue = EUR_ACCOUNT_NUMBER
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
