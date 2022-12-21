package pl.cleankod.exchange.adapter.provider.nbp;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
//import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.jupiter.api.Test;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRatesProviderIT { // Integration tests with the real NBP API.

        private static final String nbpApiBaseUrl = "http://api.nbp.pl/api";
        private final static ExchangeRatesNbpClient exchangeRatesNbpClient
            = Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class,
                        nbpApiBaseUrl);

        private static final ExchangeRatesProvider exchangeRatesProvider
            = new ExchangeRatesProvider(exchangeRatesNbpClient);


        @Test
        void getExchangeRate_should_beSuccesful() {
            Result<BigDecimal, Failure> result = exchangeRatesProvider.getExchangeRate(
                    Currency.getInstance("EUR"));

            assertTrue(result.isSuccessful());
        }

}
