package pl.cleankod;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import feign.Feign;
import feign.hc5.ApacheHttp5Client;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionCacheNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;
import java.util.concurrent.TimeUnit;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

    @Value("${app.cache.size:1000}")
    Integer cacheSize;

    @Value("${app.cache.ttl-seconds:600}")
    Integer cacheTtlSeconds;

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttp5Client())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(
            ExchangeRatesNbpClient exchangeRatesNbpClient, LoadingCache<String, RateWrapper> ratesCache
    ) {
        return new CurrencyConversionCacheNbpService(exchangeRatesNbpClient, ratesCache);
    }

    @Bean
    LoadingCache<String, RateWrapper> ratesCache(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .recordStats()
                .expireAfterWrite(cacheTtlSeconds, TimeUnit.SECONDS)
                .build(key -> exchangeRatesNbpClient.fetch("A", key));
    }

    @Bean
    MeterBinder ratesCacheSize(LoadingCache<String, RateWrapper> cache){
        return registry -> Gauge.builder("cache.rates.size", cache::estimatedSize)
                .description("Cache size")
                .register(registry);
    }

    @Bean
    MeterBinder ratesCacheHits(LoadingCache<String, RateWrapper> cache){
        return registry -> Gauge.builder("cache.rates.hits", () -> cache.stats().hitCount())
                .description("Cache hits")
                .register(registry);
    }

    @Bean
    MeterBinder ratesCacheMiss(LoadingCache<String, RateWrapper> cache){
        return registry -> Gauge.builder("cache.rates.miss", () -> cache.stats().missCount())
                .description("Cache miss")
                .register(registry);
    }
    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            FindAccountUseCase findAccountUseCase, CurrencyConversionService currencyConversionService,
            Environment environment
    ) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(findAccountUseCase, currencyConversionService, baseCurrency);
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
}
