package pl.cleankod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import pl.cleankod.exchange.core.domain.SingleValueObject;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.AccountService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.NbpApiErrorDecoder;

import java.io.IOException;
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
                .errorDecoder(new NbpApiErrorDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
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
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Bean
    AccountController accountController(AccountService findAccountService) {
        return new AccountController(findAccountService);
    }

    @Bean
    public AccountService findAccountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                             FindAccountUseCase findAccountUseCase) {
        return new AccountService(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

    @Bean
    SimpleModule singleValueObjectModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(SingleValueObject.class, new JsonSerializer<>() {
            @Override
            public void serialize(
                    SingleValueObject singleValueObject,
                    JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider
            ) throws IOException {
                jsonGenerator.writeObject(singleValueObject.value());
            }
        });
        return module;
    }
}
