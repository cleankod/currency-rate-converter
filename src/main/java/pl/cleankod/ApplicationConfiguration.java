package pl.cleankod;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.codejargon.feather.Provides;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpCache;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import uk.org.webcompere.lightweightconfig.ConfigLoader;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.Currency;
import java.util.Properties;

public class ApplicationConfiguration {

    @Provides
    @Singleton
    Properties configuration() {
        return ConfigLoader.loadPropertiesFromResource("application.properties");
    }

    @Provides
    @Singleton
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }


    @Provides
    @Singleton
    CircuitBreakerRegistry circuitBreakerRegistry() {
        //TODO: update config
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(1)
                .slidingWindowSize(2)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        return circuitBreakerRegistry;
    }

    @Provides
    @Singleton
    ExchangeRatesNbpClient exchangeRatesNbpClient(Properties configuration, CircuitBreakerRegistry circuitBreakerRegistry) {
        String nbpApiBaseUrl = configuration.getProperty("provider.nbp-api.base-url");
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("exchangeRatesNbpClient");
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .build();
        return Resilience4jFeign.builder(decorators)
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Provides
    @Singleton
    Cache<ExchangeRatesNbpCache.Key, RateWrapper> exchangeRatesNbpCache() {
        return ExchangeRatesNbpCache.create();
    }

    @Provides
    @Singleton
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache<ExchangeRatesNbpCache.Key, RateWrapper> exchangeRatesNbpCache) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, exchangeRatesNbpCache);
    }

    @Provides
    @Singleton
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            Properties configuration
    ) {
        Currency baseCurrency = Currency.getInstance(configuration.getProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Provides
    @Singleton
    AccountController accountController(ObjectMapper objectMapper, FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        return new AccountController(objectMapper, findAccountAndConvertCurrencyUseCase);
    }

    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
