package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Optional;

public interface AccountService {

	Optional<Account> findAccount(String accountId, Optional<String> currencyOpt);

	Optional<Account> findAccountByNumber(String accountNumber, Optional<String> currencyOpt);

}
