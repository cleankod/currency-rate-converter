package pl.cleankod;

import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.AccountService;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.CurrencyConverter;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClientFallback;
import pl.cleankod.exchange.provider.nbp.RatesCache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.exchange.serialization.SingleJsonValueInRecordFinder;
import pl.cleankod.exchange.serialization.SingleValueSerializer;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.util.Currency;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ApplicationInitializer {

    public static void main(String[] args) {
        run(args);
    }

    public static ApplicationContext run(String[] args) {
        return Micronaut.run(ApplicationInitializer.class, args);
    }

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    RatesCache ratesCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        MutableConfiguration<String, RateWrapper> configuration = new MutableConfiguration<String, RateWrapper>()
                .setTypes(String.class, RateWrapper.class);
        cacheManager.destroyCache("rates");
        return new RatesCache(cacheManager.createCache("rates", configuration));
    }

    @Bean
    ExchangeRatesNbpClientFallback exchangeRatesNbpClientFallback(RatesCache ratesCache) {
        return new ExchangeRatesNbpClientFallback(ratesCache);
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment, ExchangeRatesNbpClientFallback nbpClientFallback) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("NBP");

        FeignDecorators feignDecorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallbackFactory(e -> nbpClientFallback)
                .build();

        return Resilience4jFeign.builder(feignDecorators)
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConverter currencyConverter() {
        return new CurrencyConverter();
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                                        CurrencyConverter currencyConverter,
                                                        RatesCache ratesCache) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, currencyConverter, ratesCache);
    }

    @Bean
    public SimpleModule singleValueObjectModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(SingleValueRecordSerializer(Account.Id.class));
        simpleModule.addSerializer(new SingleValueSerializer<>(Account.Number.class, Account.Number::value));

        return simpleModule;
    }

    private static <T> SingleValueSerializer<T, Object> SingleValueRecordSerializer(Class<T> recordClass) {
        return new SingleValueSerializer<>(recordClass, SingleJsonValueInRecordFinder.getAccessorFor(recordClass));
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
    AccountService accountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                  FindAccountUseCase findAccountUseCase) {
        return new AccountService(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    AccountController accountController(AccountService accountService) {
        return new AccountController(accountService);
    }

}
