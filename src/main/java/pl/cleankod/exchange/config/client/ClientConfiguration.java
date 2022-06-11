package pl.cleankod.exchange.config.client;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

@Configuration
public class ClientConfiguration {
    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(final Environment environment) {
        final String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(new RetrieveMessageErrorDecoder())
                .logLevel(feign.Logger.Level.FULL)
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

}
