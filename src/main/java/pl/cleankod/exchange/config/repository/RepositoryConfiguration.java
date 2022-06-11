package pl.cleankod.exchange.config.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.exchange.core.repository.AccountInMemoryRepositoryImpl;
import pl.cleankod.exchange.core.repository.AccountRepository;

@Configuration
class RepositoryConfiguration {

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepositoryImpl();
    }

}
