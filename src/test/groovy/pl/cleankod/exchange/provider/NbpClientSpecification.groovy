package pl.cleankod.exchange.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import pl.cleankod.BaseApplicationSpecification

class NbpClientSpecification extends BaseApplicationSpecification {

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

    def "should return error message when the call to NBP API is not made correctly"() {
        given:
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(WireMock.badRequest())
        )

        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        def response = getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 500
        transformError(response).message() == "Bad request to the NBP API."
    }

    def "should return error message when the NBP API is not not found"() {
        given:
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(WireMock.notFound())
        )

        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        def response = getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 500
        transformError(response).message() == "NBP API cannot be found."
    }

    def "should return error message when the NBP API is not available"() {
        given:
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR/2022-02-08")
                        .willReturn(WireMock.serviceUnavailable())
        )

        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        def response = getResponse("/accounts/${accountId}?currency=${currency}")

        then:
        response.getStatusLine().getStatusCode() == 500
        transformError(response).message() == "NBP API is unavailable."
    }
}
