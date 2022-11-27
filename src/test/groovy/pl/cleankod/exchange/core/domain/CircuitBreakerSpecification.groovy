package pl.cleankod.exchange.core.domain

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.HttpResponse
import pl.cleankod.BaseApplicationSpecification

import java.nio.charset.StandardCharsets

class CircuitBreakerSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should not convert currency when circuit breaker is triggered"() {
        given:
        def accountNumber = URLEncoder.encode("65 1090 1665 0000 0001 0373 7343", StandardCharsets.UTF_8)
        def minimumNumberOfCallsForCircuitBreaker = 10
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/USD/" + startDate + "/" + endDate)
                        .willReturn(WireMock.badRequestEntity().withStatus(400)))

        when:
        for (int i = 1; i <= minimumNumberOfCallsForCircuitBreaker; i++) {
            getResponse("/accounts/number=${accountNumber}?currency=USD")
        }
        HttpResponse response = getResponse("/accounts/number=${accountNumber}?currency=USD")

        then:
        response.getStatusLine().getStatusCode() == 503
        transformError(response).errorMessage() == "External service is currently unavailable"
    }
}
