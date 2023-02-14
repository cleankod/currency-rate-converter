package pl.cleankod.exchange.core.usecase;

import lombok.extern.slf4j.Slf4j;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Optional;

@Slf4j
public class FindAccountUseCase {
    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> execute(Account.Id id) {
        log.info("Finding account by ID {}", id);
        return accountRepository.find(id);
    }

    public Optional<Account> execute(Account.Number number) {
        log.info("Finding account by Number {}", number;
        return accountRepository.find(number);
    }
}
