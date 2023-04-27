package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.controller.AccountController;
import pl.cleankod.exchange.entrypoint.exception.ExceptionHandlerAdvice;
import pl.cleankod.exchange.entrypoint.mapper.AccountMapper;
import pl.cleankod.exchange.entrypoint.service.AccountService;
import pl.cleankod.exchange.entrypoint.service.AccountServiceImpl;
import pl.cleankod.exchange.provider.nbp.repository.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.nbp.service.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.service.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.service.ExchangeRatesNbpClientImpl;

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
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClientImpl exchangeRatesNbpClient) {
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
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Bean
    AccountService accountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                  FindAccountUseCase findAccountUseCase) {
        return new AccountServiceImpl(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    AccountMapper accountMapper() {
        return Mappers.getMapper(AccountMapper.class);
    }

    @Bean
    AccountController accountController(AccountServiceImpl accountService, AccountMapper mapper) {
        return new AccountController(accountService, mapper);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

    @Bean
    ExchangeRatesNbpClientImpl exchangeRatesNbpClientImpl(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new ExchangeRatesNbpClientImpl(exchangeRatesNbpClient);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("provider.nbp.api.results.")
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Money converter API")
                        .description("API offering ways to convert money from a given account"));

    }

}


