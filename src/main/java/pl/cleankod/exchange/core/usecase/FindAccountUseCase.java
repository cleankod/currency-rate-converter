package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.entrypoint.exception.AccountNotFoundException;

public class FindAccountUseCase {
    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findAccountById(Account.Id id) {
        return accountRepository.find(id).orElseThrow(() -> new AccountNotFoundException(id.value().toString()));
    }

    public Account findAccountByNumber(Account.Number number) {
        return accountRepository.find(number).orElseThrow(() -> new AccountNotFoundException(number.value()));
    }
}
