package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Optional;

public interface AccountService {
		
		Optional<Account> findAccountById(String accountId, Optional<String> currencyOptional);
		
		Optional<Account> findAccountByNumber(String accountNumber, Optional<String> currencyOptional);


}