package pl.cleankod;

import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;

@Factory
public class ApplicationInitializer {
    public static void main(String[] args) {
        Micronaut.build(args)
                .deduceEnvironment(false)
                .start();
    }

    @Singleton
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Singleton
    ExchangeRatesNbpClient exchangeRatesNbpClient(@Value("${provider.nbp-api.base-url}") String nbpApiBaseUrl) {
        var clientName = "NBP";
        var circuitBreakerConfig = CircuitBreakerConfig.custom()
                // TODO it is only example, it should be adjusted to reality
                .slidingWindowSize(10)
                .failureRateThreshold(0.5f)
                .build();

        var decorators = FeignDecorators.builder()
                .withCircuitBreaker(CircuitBreaker.of(clientName, circuitBreakerConfig))
                .build();

        return Resilience4jFeign.builder(decorators)
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient);
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            @Value("${app.base-currency}") String appBaseCurrency
    ) {
        Currency baseCurrency = Currency.getInstance(appBaseCurrency);
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }
}
