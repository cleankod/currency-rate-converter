package pl.cleankod.exchange

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import pl.cleankod.SystemUnderTest
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.core.domain.Money
import spock.lang.Specification

class ExchangeApiTemporarilyNotAvailableSpecification extends Specification {

    private static WireMockServer wireMockServer = new WireMockServer(
        WireMockConfiguration.options().port(8081)
    )

    private static sut = new SystemUnderTest()

    def setupSpec() {
        sut.startWithRandomPort()
        def body = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}"
        wireMockServer.stubFor(
                WireMock.get("/exchangerates/rates/A/EUR")
                        .willReturn(WireMock.ok(body))
        )
    }

    def cleanupSpec() {
        sut.stop()
    }

    def "should return account if exchange service was available during first request and then is not available  "() {
        given:
        def accountId = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744"
        def currency = "EUR"

        when:
        wireMockServer.start()
        sut.getResponse("/accounts/${accountId}?currency=${currency}")
        wireMockServer.stop()
        def response = sut.get("/accounts/${accountId}?currency=${currency}", Account)

        then:
        response == new Account(
            Account.Id.of(accountId),
            Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
            Money.of("27.16", currency)
        )
    }
}
