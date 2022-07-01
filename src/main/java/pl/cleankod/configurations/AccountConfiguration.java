package pl.cleankod.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.usecase.AccountService;
import pl.cleankod.exchange.core.usecase.AccountServiceImpl;
import pl.cleankod.exchange.core.usecase.ConvertAccountCurrencyService;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;

@Configuration
public class AccountConfiguration {

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    AccountService accountService(@Autowired AccountRepository accountRepository,
                                  @Autowired ConvertAccountCurrencyService convertAccountCurrencyService) {
        return new AccountServiceImpl(accountRepository, convertAccountCurrencyService);
    }
}
