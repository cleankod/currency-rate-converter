package pl.cleankod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;

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
    AccountController accountController(AccountRepository accountRepository) {
        return new AccountController(accountRepository);
    }
}
