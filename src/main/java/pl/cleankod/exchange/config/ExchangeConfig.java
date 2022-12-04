package pl.cleankod.exchange.config;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.AccountService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CircuitBreakerNbpServiceProxy;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.time.Duration;
import java.util.Currency;

@EnableCaching
@Configuration
class ExchangeConfig {

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
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                                        CircuitBreaker circuitBreaker) {
        return new CircuitBreakerNbpServiceProxy(circuitBreaker,
                new CurrencyConversionNbpService(exchangeRatesNbpClient));
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            Environment environment
    ) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(20)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .build();
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("circuitBreaker");
    }

    @Bean
    AccountService accountService(FindAccountUseCase findAccountUseCase, FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        return new AccountService(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }
}


