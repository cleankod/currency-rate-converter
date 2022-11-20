package pl.cleankod.exchange.provider

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.http.util.EntityUtils
import pl.cleankod.BaseApplicationSpecification
import pl.cleankod.exchange.core.domain.Money
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient
import pl.cleankod.exchange.provider.nbp.model.Rate
import pl.cleankod.exchange.provider.nbp.model.RateWrapper

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

class CurrencyConversionNbpServiceSpecification extends BaseApplicationSpecification {

    private static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(8081)
    )

    def setupSpec() {
        wireMockServer.start()
        configureFor("localhost", wireMockServer.port());

        def body = "{\"table\":\"A\",\"currency\":\"ron\",\"code\":\"RON\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":4.5452}]}"
        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/RON/2022-02-08").willReturn(WireMock.ok(body)))

        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/GBP/2022-02-08")
                .willReturn(WireMock.serverError().withStatus(500).withStatusMessage("Server error message")))

        wireMockServer.stubFor(WireMock.get("/exchangerates/rates/A/USD/2022-02-08")
                .willReturn(WireMock.serverError().withBody("{ \"status\" : 500, \"error\" : \"Internal Server Error\", \"message\" : \"Server error message\" }")))
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should return from cache"() {
        when:
        def response1 = getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=RON")
        def response2 = getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=RON")

        then:
        response1.getStatusLine().getStatusCode() == response2.getStatusLine().getStatusCode()
        EntityUtils.toString(response1.getEntity(), "UTF-8") == EntityUtils.toString(response2.getEntity(), "UTF-8")
        verify(1, getRequestedFor(urlEqualTo("/exchangerates/rates/A/RON/2022-02-08")));
    }

    def "should throw an error"() {
        when:
        def response = getResponse("/accounts/fa07c538-8ce4-11ec-9ad5-4f5a625cd744?currency=${currency}")

        then:
        response.getStatusLine().statusCode == statusCode
        EntityUtils.toString(response.getEntity(), "UTF-8") == message

        where:
        currency || statusCode || message
        "GBP"    || 500        || "{\"status\":500,\"error\":null,\"message\":\"Server error message\"}"
        "USD"    || 500        || "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Server error message\"}"
    }

    def "should convert amount using scale"() {
        given:
        ExchangeRatesNbpClient exchangeRatesNbpClient = Mock(ExchangeRatesNbpClient.class)
        exchangeRatesNbpClient.fetch(_ as String, _ as String) >> {
            return new RateWrapper("rate", "EUR", "EUR", [new Rate(_ as String, _ as String, BigDecimal.valueOf(4.5274))])
        }

        CurrencyConversionNbpService currencyConversionNbpService = new CurrencyConversionNbpService(exchangeRatesNbpClient)

        when:
        Money money = currencyConversionNbpService.convert(Money.of(givenAmount, givenCurrency), Currency.getInstance("EUR"))

        then:
        money != null
        money.amount() == expectedAmount
        money.currency() == expectedCurrency

        where:
        givenAmount               | givenCurrency               || expectedAmount               || expectedCurrency
        "1"                       | "PLN"                       || BigDecimal.valueOf(0.22)     || Currency.getInstance("EUR")
    }
}
