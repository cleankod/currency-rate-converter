package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import pl.cleankod.exchange.core.AccountService;
import pl.cleankod.exchange.core.repository.AccountRepository;
import pl.cleankod.exchange.core.service.AccountServiceImpl;
import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.nbp.service.CurrencyConversionNbpServiceImpl;
import pl.cleankod.exchange.provider.nbp.client.ExchangeRatesNbpClient;

import javax.enterprise.inject.Produces;
import java.util.Currency;

@QuarkusMain
public class ApplicationInitializer {

    public static void main(String ... args) {
        Quarkus.run(args);
    }

    @Produces
    @DefaultBean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Produces
    @DefaultBean
    ExchangeRatesNbpClient exchangeRatesNbpClient(@ConfigProperty(name = "provider.nbp-api.base-url") String nbpApiBaseUrl) {
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Produces
    @DefaultBean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpServiceImpl(exchangeRatesNbpClient);
    }

    @Produces
    @DefaultBean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
        return new FindAccountUseCase(accountRepository);
    }

    @Produces
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            @ConfigProperty(name = "app.base-currency") String baseCurrency
    ) {
        Currency currency = Currency.getInstance(baseCurrency);
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, currency);
    }

    @Produces
    @DefaultBean
    AccountService accountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                  FindAccountUseCase findAccountUseCase) {
        return new AccountServiceImpl(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Produces
    @DefaultBean
    AccountController accountController(AccountService accountService) {
        return new AccountController(accountService);
    }

    @Produces
    @DefaultBean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

}
