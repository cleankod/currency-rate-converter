package pl.cleankod;

import feign.httpclient.ApacheHttpClient;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.adapter.persistence.AccountRepositoryAdapter;
import pl.cleankod.exchange.adapter.persistence.repository.AccountRepository;

import pl.cleankod.exchange.adapter.provider.nbp.ExchangeRatesProvider;
import pl.cleankod.exchange.adapter.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;
import pl.cleankod.exchange.core.service.AccountService;
import pl.cleankod.exchange.core.service.CurrencyConversionService;
import pl.cleankod.exchange.adapter.entrypoint.AccountController;
import pl.cleankod.exchange.adapter.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.adapter.persistence.repository.AccountInMemoryRepository;


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
    AccountRepositoryAdapter accountRepositoryAdapter(AccountRepository accountRepository) {
        return new AccountRepositoryAdapter(accountRepository);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("exchangeRates");
    }
    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return HystrixFeign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class,
                        nbpApiBaseUrl); //, ExchangeRatesNbpClientFallbackFactory.fallbackFactory);
    }

    @Bean
    ExchangeRatesProvider exchangeRatesProvider(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new ExchangeRatesProvider(exchangeRatesNbpClient);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesProviderPort exchangeRatesProvider) {
        return new CurrencyConversionService(exchangeRatesProvider);
    }

    @Bean
    AccountService accountService(
            AccountRepositoryAdapter accountRepositoryAdapter,
            CurrencyConversionService currencyConversionService,
            Environment environment
    ) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new AccountService(currencyConversionService, baseCurrency, accountRepositoryAdapter);
    }

    @Bean
    AccountController accountController(AccountService accountService) {
        return new AccountController(accountService);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }
}
