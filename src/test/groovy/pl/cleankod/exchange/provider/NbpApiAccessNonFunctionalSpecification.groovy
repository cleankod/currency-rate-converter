package pl.cleankod.exchange.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import pl.cleankod.BlackBox
import pl.cleankod.exchange.core.domain.Account
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class NbpApiAccessNonFunctionalSpecification extends Specification {
    private BlackBox blackBox = new BlackBox()
    private WireMockServer nbp = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setup() {
        blackBox = new BlackBox();
        nbp = new WireMockServer(WireMockConfiguration.options().port(8081))

        nbp.start()
        blackBox.start()
    }

    def cleanup() {
        blackBox.stop()
        nbp.stop()
    }

    def "should block calls to NBP API when encountering too many failures"() {
        given:
        nbp.stubFor(WireMock.get(anyUrl()).willReturn(serverError()))
        int aLotOfRequests = 15

        when:
        (1..aLotOfRequests).each {
            blackBox.getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=USD")
        }

        then:
        def numberOfSentRequests = nbp.findAll(getRequestedFor(anyUrl())).size()
        numberOfSentRequests in 2..aLotOfRequests
    }

    def "should use cached exchange rate for subsequent failed requests to NBP"() {
        given:
        def body = '{"table":"A","currency":"usd","code":"USD","rates":[{"no":"026/A/NBP/2022","effectiveDate":"2022-02-08","mid":4}]}'
        nbp.stubFor(WireMock.get("/exchangerates/rates/A/USD").willReturn(ok(body)))

        when:

        def firstResponse = blackBox.get("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=USD", Account)
        def secondResponse = blackBox.get("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=USD", Account)

        then:
        secondResponse == firstResponse
        def numberOfSentRequests = nbp.findAll(getRequestedFor(anyUrl())).size()
        numberOfSentRequests == 1
    }
}
