package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.ApplicationError;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.util.domain.Result;

public class FindAccountUseCase {
    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<Account, ApplicationError> execute(Account.Id id) {
        return accountRepository.find(id)
                .map(Result::<Account, ApplicationError>successful)
                .orElse(Result.fail(new AccountNotFoundError(id)));
    }

    public Result<Account, ApplicationError> execute(Account.Number number) {
        return accountRepository.find(number)
                .map(Result::<Account, ApplicationError>successful)
                .orElse(Result.fail(new AccountNotFoundError(number)));
    }
}
