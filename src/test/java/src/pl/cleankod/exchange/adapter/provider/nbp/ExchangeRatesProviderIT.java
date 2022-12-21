package pl.cleankod.exchange.adapter.provider.nbp;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.jupiter.api.Test;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClientFallbackFactory;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRatesProviderIT { // Integration tests with the real NBP API.

        private static final String nbpApiBaseUrl = "http://api.nbp.pl/api";
        private static final String inExistentUrl ="http://rxdtcfygvbhnl.km/";
        private final static ExchangeRatesNbpClient exchangeRatesNbpClient
            = Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class,
                        nbpApiBaseUrl);

        private final static ExchangeRatesNbpClient exchangeRatesNbpClientWithFallback
                = HystrixFeign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class,
                        inExistentUrl, ExchangeRatesNbpClientFallbackFactory.fallbackFactory);

        private static final ExchangeRatesProvider exchangeRatesProvider
            = new ExchangeRatesProvider(exchangeRatesNbpClient);

        private static final ExchangeRatesProvider exchangeRatesProviderWithFallback
            = new ExchangeRatesProvider(exchangeRatesNbpClientWithFallback);


        @Test
        void getExchangeRate_should_beSuccesful() {
            Result<BigDecimal, Failure> result = exchangeRatesProvider.getExchangeRate(
                    Currency.getInstance("EUR"));

            assertTrue(result.isSuccessful());
        }

        @Test
        void getExchangeRate_should_fail() {
            Result<BigDecimal, Failure> result = exchangeRatesProvider.getExchangeRate(
                    Currency.getInstance("PLN"));  // api doesn't return an exchange rate from PLN to PLN (404)

            assertTrue(result.isFail());
        }

        @Test
        void given_inExistentUrl_and_clientWithFallback_getExchangeRate_should_beSuccessful() {
            Result<BigDecimal, Failure> result = exchangeRatesProviderWithFallback.getExchangeRate(
                    Currency.getInstance("EUR"));

            assertTrue(result.isSuccessful());
            assertEquals(ExchangeRatesNbpClientFallbackFactory.fallBackValue, result.successfulValue().doubleValue());
        }


}
