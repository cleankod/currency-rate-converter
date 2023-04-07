package pl.cleankod.exchange.provider.nbp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.HttpResponse
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.domain.Account

import static com.github.tomakehurst.wiremock.client.WireMock.*

class ExchangeRatesNbpClientSpecification extends BaseApplicationSpecification {
    private static def body = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}";

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()
        configureFor(wireMockServer.port())

    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def cleanup() {
        wireMockServer.resetAll()
    }

    def "should cache request to nbpApi"() {
        wireMockServer.stubFor(
                get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(ok(body)))


        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        def firstCall = get("/accounts/${accountId}?currency=${currency}", Account)
        and:
        def secondCall = get("/accounts/${accountId}?currency=${currency}", Account)
        and:
        def thirdCall = get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        verify(exactly(1), getRequestedFor(urlEqualTo("/exchangerates/rates/A/EUR/2022-02-08")))
    }

    def "should handle NPB API bad request error "() {
        wireMockServer.stubFor(
                get("/exchangerates/rates/A/GBP/2022-02-08")
                        .willReturn(badRequest()))

        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "GBP"

        when:
        HttpResponse response = getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 500
        transformError(response).message() == "Bad request to NBP API"
    }

    def "should handle NPB API Service unavailable error "() {
        wireMockServer.stubFor(
                get("/exchangerates/rates/A/USD/2022-02-08")
                        .willReturn(serviceUnavailable()))

        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "USD"

        when:
        HttpResponse response = getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 500
        transformError(response).message() == "NBP Service Unavailable"
    }
}
