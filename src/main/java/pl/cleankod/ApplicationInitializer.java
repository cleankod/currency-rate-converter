package pl.cleankod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
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
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.account.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.conversion.CurrencyConversionServiceProvider;
import pl.cleankod.exchange.provider.conversion.nbp.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.conversion.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.conversion.nbp.client.ExchangeRatesNbpClientCachingWrapper;
import pl.cleankod.exchange.provider.conversion.nbp.client.ExchangeRatesNbpClientCircuitBreakerWrapper;

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
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionNbpService nbpConversionService(ExchangeRatesNbpClient client, Environment environment) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));

        // here we wire up the wrapper in the order we wish, but the best usage is achieved by having the caching wrapper
        // as the outmost layer in the chain
        var circuitBreakerWrapper = new ExchangeRatesNbpClientCircuitBreakerWrapper(client);
        var cachingWrapper = new ExchangeRatesNbpClientCachingWrapper(circuitBreakerWrapper);

        return new CurrencyConversionNbpService(cachingWrapper, baseCurrency);
    }

    @Bean
    CurrencyConversionServiceProvider currencyConversionService(CurrencyConversionNbpService nbpService) {
        CurrencyConversionServiceProvider currencyConversions = new CurrencyConversionServiceProvider();
        currencyConversions.addCurrencyConverter(nbpService);
        return currencyConversions;
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            FindAccountUseCase findAccountUseCase,
            CurrencyConversionService currencyConversionService
    ) {
        return new FindAccountAndConvertCurrencyUseCase(findAccountUseCase, currencyConversionService);
    }

    @Bean
    AccountController accountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        return new AccountController(findAccountAndConvertCurrencyUseCase);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

    @Bean
    public SimpleModule singleValueObjectModule() {
        SimpleModule module = new SimpleModule();

        module.addSerializer(Account.Number.class, new JsonSerializer<>() {
            @Override
            public void serialize(Account.Number accountNumber, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeObject(accountNumber.value());
            }
        });

        module.addSerializer(Account.Id.class, new JsonSerializer<>() {
            @Override
            public void serialize(Account.Id accountId, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeObject(accountId.value());
            }
        });

        return module;
    }
}
