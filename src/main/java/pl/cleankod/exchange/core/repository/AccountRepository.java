package pl.cleankod.exchange.core.repository;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> find(UUID id);
    Optional<Account> find(String number);
}
