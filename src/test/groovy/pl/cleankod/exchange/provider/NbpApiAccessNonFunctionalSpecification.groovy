package pl.cleankod.exchange.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import pl.cleankod.BlackBox
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class NbpApiAccessNonFunctionalSpecification extends Specification {
    private static BlackBox blackBox = new BlackBox()
    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()
        wireMockServer.stubFor(WireMock.get(anyUrl()).willReturn(serverError()))

        blackBox.start()
    }

    def cleanupSpec() {
        blackBox.stop()
        wireMockServer.stop()
    }

    def "should block calls to NBP API when encountering too many failures"() {
        given:
        int aLotOfRequests = 15

        when:
        (1..aLotOfRequests).each {
            blackBox.getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=USD")
        }

        then:
        def numberOfSentRequests = wireMockServer.findAll(getRequestedFor(anyUrl())).size()
        numberOfSentRequests in 2..aLotOfRequests
    }
}
