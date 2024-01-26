package pl.cleankod.exchange.provider.nbp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import feign.Feign
import feign.httpclient.ApacheHttpClient
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import pl.cleankod.exchange.provider.nbp.exceptions.ClientAuthorizationException
import pl.cleankod.exchange.provider.nbp.exceptions.CurrencyNotFoundException
import pl.cleankod.exchange.provider.nbp.exceptions.InvalidCurrencyRequestException
import pl.cleankod.exchange.provider.nbp.exceptions.NbpServerException
import spock.lang.Specification
import static com.github.tomakehurst.wiremock.client.WireMock.*

class ExchangeRatesNbpClientSpec extends Specification {

    private static WireMockServer wireMockServer
    private static ExchangeRatesNbpClient client

    def setupSpec() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort())
        wireMockServer.start()
        configureFor("localhost", wireMockServer.port())

        String wireMockUrl = "http://localhost:" + wireMockServer.port()
        client = Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(new CustomNbpErrorDecoder())
                .target(ExchangeRatesNbpClient.class, wireMockUrl)
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def "should throw InvalidCurrencyRequestException for bad request"() {
        given:
        stubFor(get(urlEqualTo("/exchangerates/rates/A/INVALID/2022-02-08"))
                .willReturn(aResponse().withStatus(400)))

        when:
        client.fetch("A", "INVALID")

        then:
        thrown(InvalidCurrencyRequestException)

        cleanup:
        wireMockServer.resetMappings()
    }

    def "should throw CurrencyNotFoundException when currency not found"() {
        given:
        stubFor(get(urlEqualTo("/exchangerates/rates/A/USD/2022-02-08"))
                .willReturn(aResponse().withStatus(404)))

        when:
        client.fetch("A", "USD")

        then:
        thrown(CurrencyNotFoundException)

        cleanup:
        wireMockServer.resetMappings()
    }

    def "should throw ClientAuthorizationException for unauthorized access"() {
        given:
        stubFor(get(urlEqualTo("/exchangerates/rates/A/UNAUTHORIZED/2022-02-08"))
                .willReturn(aResponse().withStatus(401)))

        when:
        client.fetch("A", "UNAUTHORIZED")

        then:
        thrown(ClientAuthorizationException)

        cleanup:
        wireMockServer.resetMappings()
    }

    def "should throw ClientAuthorizationException for forbidden access"() {
        given:
        stubFor(get(urlEqualTo("/exchangerates/rates/A/FORBIDDEN/2022-02-08"))
                .willReturn(aResponse().withStatus(403)))

        when:
        client.fetch("A", "FORBIDDEN")

        then:
        thrown(ClientAuthorizationException)

        cleanup:
        wireMockServer.resetMappings()
    }

    def "should throw NbpServerException for server error"() {
        given:
        stubFor(get(urlEqualTo("/exchangerates/rates/A/SERVERERROR/2022-02-08"))
                .willReturn(aResponse().withStatus(500)))

        when:
        client.fetch("A", "SERVERERROR")

        then:
        thrown(NbpServerException)

        cleanup:
        wireMockServer.resetMappings()
    }

}
