package pl.cleankod.exchange.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.util.EntityUtils
import pl.cleankod.BaseApplicationSpecification

class NbpServiceSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()

        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/EUR/2022-02-08").willReturn(WireMock.serverError()))
        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/USD/2022-02-08").willReturn(WireMock.serviceUnavailable()))
        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/JPY/2022-02-08").willReturn(WireMock.badRequest()))
        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/GBP/2022-02-08").willReturn(WireMock.forbidden()))
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should throw an error"() {
        when:
        def response = getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=${currency}")

        then:
        response.getStatusLine().statusCode == statusCode
        EntityUtils.toString(response.getEntity(), "UTF-8") == message

        where:
        currency || statusCode || message
        "EUR"    || 500        || "{\"message\":\"Server failed to fulfill a valid request due to an error with server\"}"
        "USD"    || 503        || "{\"message\":\"Server failed to fulfill a valid request due to an error with server\"}"
        "JPY"    || 400        || "{\"message\":\"Client sent an invalid request\"}"
        "GBP"    || 403        || "{\"message\":\"Client sent an invalid request\"}"
    }
}
