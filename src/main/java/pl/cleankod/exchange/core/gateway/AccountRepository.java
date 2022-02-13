package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> find(Account.Id id);
    Optional<Account> find(Account.Number number);
}
