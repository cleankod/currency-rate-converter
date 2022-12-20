package pl.cleankod.exchange.adapter.persistence;

import pl.cleankod.exchange.adapter.persistence.repository.AccountRepository;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.port.AccountRepositoryPort;

import java.util.Optional;

public class AccountRepositoryAdapter implements AccountRepositoryPort {

    // Suggestion: if the persistence layer returns its own Entities, then the current
    // class has a usage in terms of linking/ adapting to the business layer by encapsulating
    // the conversion between entity and domain objects, i.e., performing mapping logic
    private final AccountRepository accountRepository;

    public AccountRepositoryAdapter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> find(Account.Id id) {
        return accountRepository.find(id);
    }

    @Override
    public Optional<Account> find(Account.Number number) {
        return accountRepository.find(number);
    }
}
