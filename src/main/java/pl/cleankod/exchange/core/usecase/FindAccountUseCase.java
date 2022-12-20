package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.port.AccountRepositoryPort;

import java.util.Optional;

public class FindAccountUseCase {
    private final AccountRepositoryPort accountRepositoryAdapter;

    public FindAccountUseCase(AccountRepositoryPort accountRepositoryAdapter) {
        this.accountRepositoryAdapter = accountRepositoryAdapter;
    }

    public Optional<Account> execute(Account.Id id) {
        return accountRepositoryAdapter.find(id);
    }

    public Optional<Account> execute(Account.Number number) {
        return accountRepositoryAdapter.find(number);
    }
}
