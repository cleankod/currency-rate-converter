package pl.cleankod.exchange.core;

import io.smallrye.common.constraint.Nullable;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.util.UUID;

public interface AccountService {

    Result<Account, AccountRetrievalFailedReason> findAccountById(UUID id, @Nullable String currency);

    Result<Account, AccountRetrievalFailedReason> findAccountByNumber(String number, @Nullable String currency);
}
