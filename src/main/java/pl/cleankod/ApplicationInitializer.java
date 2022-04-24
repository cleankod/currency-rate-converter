package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.runtime.Micronaut;
import io.micronaut.web.router.DefaultRouter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.GlobalExceptionHandler;
import pl.cleankod.exchange.entrypoint.number.NumberRewriteRouter;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;

@OpenAPIDefinition(
        info = @Info(
                title = "Currency Rate Converter",
                version = "0.1",
                description = "Currency Rate Converter"
        )
)
@Factory
public class ApplicationInitializer {

    public static void main(String[] args) {
        Micronaut.run(ApplicationInitializer.class, args);
    }

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(@Property(name = "provider.nbp-api.base-url") String nbpApiBaseUrl) {
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
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
            @Property(name = "app.base-currency") String baseCurrency
    ) {
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService,
                Currency.getInstance(baseCurrency));
    }

    @Bean
    AccountController accountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                        FindAccountUseCase findAccountUseCase) {
        return new AccountController(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Singleton
    GlobalExceptionHandler<CurrencyConversionException> currencyConversionExceptionHandler() {
        return new GlobalExceptionHandler<>();
    }

    @Singleton
    GlobalExceptionHandler<IllegalArgumentException> illegalArgumentExceptionHandler() {
        return new GlobalExceptionHandler<>();
    }
}
