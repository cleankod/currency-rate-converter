package pl.cleankod.exchange.provider.config;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.Cache;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.CurrencyConversionStubService;
import pl.cleankod.exchange.provider.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.NbpLFUCache;

@SpringBootConfiguration
public class ProviderConfig {
    @Bean
    CurrencyConversionService currencyConversionStubService() {
        return new CurrencyConversionStubService();
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache cache) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, cache);
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    Cache cache(Environment environment) {
        return new NbpLFUCache(environment.getRequiredProperty("cache.maxCacheSize", Integer.class));
    }

}
