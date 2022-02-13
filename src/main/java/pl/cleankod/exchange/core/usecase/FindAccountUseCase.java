package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Optional;

public class FindAccountUseCase {
    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> execute(Account.Id id) {
        return accountRepository.find(id);
    }

    public Optional<Account> execute(Account.Number number) {
        return accountRepository.find(number);
    }
}
