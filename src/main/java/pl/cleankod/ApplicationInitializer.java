package pl.cleankod;

import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.config.NbpClientErrorDecoder;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.ExchangeFacade;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.NbpClientAdapter;

import java.time.Duration;
import java.util.Currency;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableCaching
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(
            @Value("${provider.nbp-api.base-url}") String nbpApiBaseUrl,
            FeignDecorators feignDecorators
    ) {
        return Resilience4jFeign.builder(feignDecorators)
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(new NbpClientErrorDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    FeignDecorators feignDecorators(
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        return FeignDecorators.builder()
                .withCircuitBreaker(circuitBreakerRegistry.circuitBreaker("nbpClientCircuitBreaker"))
//                .withFallbackFactory(ExchangeRatesNbpClientFallback::new)
                .build();
    }

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry(
            CircuitBreakerConfig circuitBreakerConfig
    ) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(60)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(5))
                .permittedNumberOfCallsInHalfOpenState(2)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(2)
                .build();
    }

    @Bean
    CurrencyConversionService currencyConversionService(
            NbpClientAdapter nbpClientAdapter
    ) {
        return new CurrencyConversionNbpService(nbpClientAdapter);
    }

    @Bean
    NbpClientAdapter nbpClientAdapter(
            ExchangeRatesNbpClient exchangeRatesNbpClient
    ) {
        return new NbpClientAdapter(exchangeRatesNbpClient);
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
    ExchangeFacade exchangeFacade(
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
            FindAccountUseCase findAccountUseCase
    ) {
        return new ExchangeFacade(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    AccountController accountController(ExchangeFacade exchangeFacade) {
        return new AccountController(exchangeFacade);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }
}
