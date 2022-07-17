package pl.cleankod.exchange.core.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.AccountService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.AccountServiceImpl;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.entrypoint.model.ValidationException;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;

import java.util.Currency;

@SpringBootConfiguration
public class ExchangeConfig {

    @Bean
    AccountController accountController(AccountService accountService) {
        return new AccountController(accountService);
    }

    @Bean
    AccountService accountService(AccountRepository accountRepository,
                                  CurrencyConversionService currencyConversionService,
                                  Environment environment) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new AccountServiceImpl(accountRepository, currencyConversionService, baseCurrency);
    }

    @Bean
    AccountRepository accountRepository() throws ValidationException {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

}
