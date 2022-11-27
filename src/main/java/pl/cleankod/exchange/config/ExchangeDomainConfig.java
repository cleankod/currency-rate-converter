package pl.cleankod.exchange.config;

import java.util.Currency;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.ConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.CurrencyConversionStubService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;

@Configuration
@EnableCaching
public class ExchangeDomainConfig {

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
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
    @ConditionalOnProperty(value = "nbp.api.enable", havingValue = "true", matchIfMissing = true)
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, circuitBreakerRegistry);
    }

    @Bean
    @ConditionalOnProperty(value = "nbp.api.enable", havingValue = "false")
    CurrencyConversionService currencyConversionServiceStub() {
        return new CurrencyConversionStubService();
    }

    @Bean
    FindAccountUseCase findAccountAndConvertCurrencyUseCase(
        AccountRepository accountRepository,
        ConvertCurrencyUseCase convertCurrencyUseCase) {
        return new FindAccountUseCase(accountRepository, convertCurrencyUseCase);
    }

    @Bean
    ConvertCurrencyUseCase convertCurrencyUseCase(CurrencyConversionService currencyConversionService, Environment environment) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new ConvertCurrencyUseCase(currencyConversionService, baseCurrency);
    }

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry(Environment environment) {
        final int slidingWindowSize = Integer.parseInt(environment.getRequiredProperty("circuitBreaker.slidingWindowSize"));
        final float failureRateThreshold = Float.parseFloat(environment.getRequiredProperty("circuitBreaker.failureRateThreshold"));

        final var config = CircuitBreakerConfig.custom()
            .slidingWindowType(COUNT_BASED)
            .slidingWindowSize(slidingWindowSize)
            .failureRateThreshold(failureRateThreshold)
            .build();

        return CircuitBreakerRegistry.of(config);
    }
}
