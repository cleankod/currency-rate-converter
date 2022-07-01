package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountOperationFailedReason;
import pl.cleankod.util.domain.Result;

public interface AccountRepository {
    Result<Account, AccountOperationFailedReason> find(Account.Id id);
    Result<Account, AccountOperationFailedReason> find(Account.Number number);
}
