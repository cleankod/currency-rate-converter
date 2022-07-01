package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountOperationFailedReason;
import pl.cleankod.util.domain.Result;

public interface AccountService {
    Result<Account, AccountOperationFailedReason> getByIdWithDesiredCurrency(Account.Id id, String currency);

    Result<Account, AccountOperationFailedReason> getByNumberWithDesiredCurrency(Account.Number number, String currency);
}
