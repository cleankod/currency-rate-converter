package pl.cleankod;

import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringApplication;
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
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.serialization.SingleJsonValueInRecordFinder;
import pl.cleankod.exchange.serialization.SingleValueSerializer;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.CurrencyConverter;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

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
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConverter currencyConverter() {
        return new CurrencyConverter();
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient, CurrencyConverter currencyConverter) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, currencyConverter);
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

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

}
