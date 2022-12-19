package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.exceptions.NbpErrorDecoder;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountService;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.exchange.provider.ExchangeRatesNbpService;
import pl.cleankod.exchange.provider.ExchangeRatesService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

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
                .errorDecoder(new NbpErrorDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    ExchangeRatesService exchangeRatesNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new ExchangeRatesNbpService(exchangeRatesNbpClient);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpService exchangeRatesNbpService) {
        return new CurrencyConversionService(exchangeRatesNbpService);
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
    FindAccountService findAccountService(
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
            FindAccountUseCase findAccountUseCase
    ) {
        return new FindAccountService(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    AccountController accountController(FindAccountService findAccountService) {
        return new AccountController(findAccountService);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }
}
