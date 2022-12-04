package pl.cleankod;

import feign.Feign;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.AccountUseCase;
import pl.cleankod.exchange.core.usecase.ConvertMoneyByCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ApiExceptionHandler;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.RetreiveMessageErrorDecoder;

import java.util.Currency;

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
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(new RetreiveMessageErrorDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                                        Environment environment) {
        long cacheTimeToLiveInSecods =Long.parseLong(environment.getRequiredProperty("cache-manager.time-to-live-in-seconds"));
        return new CurrencyConversionNbpService(exchangeRatesNbpClient,cacheTimeToLiveInSecods);
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    ConvertMoneyByCurrencyUseCase convertMoneyByCurrencyUseCase(
            CurrencyConversionService currencyConversionService,
            Environment environment
    ) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new ConvertMoneyByCurrencyUseCase(currencyConversionService, baseCurrency);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            ConvertMoneyByCurrencyUseCase convertMoneyByCurrencyUseCase
    ) {
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, convertMoneyByCurrencyUseCase);
    }

    @Bean
    AccountUseCase accountUseCase(
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
            FindAccountUseCase findAccountUseCase) {
        return new AccountUseCase(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    AccountController accountController(AccountUseCase accountUseCase) {
        return new AccountController(accountUseCase);
    }

    @Bean
    ApiExceptionHandler restExceptionHandlerAdvice() {
        return new ApiExceptionHandler();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetreiveMessageErrorDecoder();
    }
}
