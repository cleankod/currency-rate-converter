package pl.cleankod;

import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountDeserializer;
import pl.cleankod.exchange.core.domain.Currency;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.time.Duration;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }
    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("exchangeRatesCircuitBreaker",
                CircuitBreakerConfig.custom()
                        .minimumNumberOfCalls(environment.getProperty("circuit-breaker.minimum-number-of-calls", Integer.class))
                        .slidingWindowSize(environment.getProperty("circuit-breaker.sliding-window-size", Integer.class))
                        .waitIntervalFunctionInOpenState(IntervalFunction.of(Duration.ofSeconds(environment.getProperty("circuit-breaker.seconds-in-open-state", Integer.class))))
                        .build()
        );
        if(environment.getProperty("circuit-breaker.disabled", Boolean.class)){
            circuitBreaker.transitionToDisabledState();
        }
        return Resilience4jFeign.builder(FeignDecorators.builder()
                        .withCircuitBreaker(circuitBreaker).build())
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, environment.getRequiredProperty("provider.nbp-api.base-url"));
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
            Environment environment
    ) {
        Currency baseCurrency = Currency.fromString(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Bean
    AccountController accountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                        FindAccountUseCase findAccountUseCase) {
        return new AccountController(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Currancy rate Converter API")
                        .description("This is a simple currency rate converter")
                        .contact(new Contact().email("some@email"))
                        .license(new License().name("license"))
                        .version("1.0")
                );
    }

    @Bean
    public SimpleModule accountDeserializer() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Account.class, new AccountDeserializer());
        return module;
    }
}
