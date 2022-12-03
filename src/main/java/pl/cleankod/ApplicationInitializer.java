package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.AccountProcess;
import pl.cleankod.exchange.core.usecase.CurrencyConversionServiceSelector;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableCaching
@SpringBootConfiguration
@EnableAutoConfiguration
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

    @Value("${app.base-currency}")
    private String nbpApiBaseCurrency;

    @Value("${provider.nbp-api.base-url}")
    private String nbpApiBaseUrl;

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(new ConcurrentMapCache("NbpRates")));
        return cacheManager;
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionNbpService currencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        Currency baseCurrency = Currency.getInstance(nbpApiBaseCurrency);
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, baseCurrency);
    }

    @Bean
    CurrencyConversionServiceSelector currencyConversionServiceSelector(CurrencyConversionNbpService currencyConversionNbpService) {
        Map<Currency, CurrencyConversionService> currencyConversionServices = new HashMap<>();
        currencyConversionServices.put(currencyConversionNbpService.getBaseCurrency(), currencyConversionNbpService);
        return new CurrencyConversionServiceSelector(currencyConversionServices);
    }

    @Bean
    AccountProcess accountProcess(AccountRepository accountRepository, CurrencyConversionServiceSelector currencyConversionServiceSelector) {
        return new AccountProcess(accountRepository, currencyConversionServiceSelector);
    }

    @Bean
    AccountController accountController(AccountProcess accountProcess) {
        return new AccountController(accountProcess);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }
}
