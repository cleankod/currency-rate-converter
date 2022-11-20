package pl.cleankod.exchange.provider.nbp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.util.EntityUtils
import pl.cleankod.BaseApplicationSpecification

class NbpApiSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()

        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/EUR/2022-02-08")
                .willReturn(WireMock.serverError().withStatus(500).withStatusMessage("Server error message")))

        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/USD/2022-02-08")
                .willReturn(WireMock.serverError().withBody("{ \"status\" : 500, \"error\" : \"Internal Server Error\", \"message\" : \"Server error message\" }")));
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
        "EUR"    || 500        || "{\"status\":500,\"error\":null,\"message\":\"Server error message\"}"
        "USD"    || 500        || "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Server error message\"}"
    }

}
