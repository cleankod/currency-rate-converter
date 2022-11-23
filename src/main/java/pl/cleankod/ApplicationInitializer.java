package pl.cleankod;

import feign.Feign;
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
import pl.cleankod.exchange.core.usecase.AccountProcess;
import pl.cleankod.exchange.core.usecase.CurrencyConversionServiceSelector;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

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
    CurrencyConversionNbpService currencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Environment environment) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("provider.nbp-api.base-currency"));
        return new CurrencyConversionNbpService(exchangeRatesNbpClient, baseCurrency);
    }

    @Bean
    CurrencyConversionServiceSelector currencyConversionServiceSelector(CurrencyConversionNbpService currencyConversionNbpService) {
        Map<Currency, CurrencyConversionService> currencyConversionServices = new HashMap<>();
        currencyConversionServices.put(currencyConversionNbpService.getBaseCurrency(), currencyConversionNbpService);
        return new CurrencyConversionServiceSelector(currencyConversionServices, currencyConversionNbpService);
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
