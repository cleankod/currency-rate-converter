package pl.cleankod.exchange.core.port;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Optional;

public interface AccountRepositoryPort {
    Optional<Account> find(Account.Id id);
    Optional<Account> find(Account.Number number);
}
