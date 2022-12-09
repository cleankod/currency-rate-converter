package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.repository.AccountRepository;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.util.UUID;

public class FindAccountUseCase {
    private final AccountRepository accountRepository;

    public FindAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Result<Account, AccountRetrievalFailedReason> execute(UUID id) {
        return accountRepository.find(id)
                .map(Result::<Account, AccountRetrievalFailedReason>successful)
                .orElse(failedAccountNotFound());
    }

    public Result<Account, AccountRetrievalFailedReason> execute(String number) {
        return accountRepository.find(number)
                .map(Result::<Account, AccountRetrievalFailedReason>successful)
                .orElse(failedAccountNotFound());
    }

    protected Result<Account, AccountRetrievalFailedReason> failedAccountNotFound() {
        return Result.fail(AccountRetrievalFailedReason.ACCOUNT_NOT_FOUND);
    }
}
