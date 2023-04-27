package pl.cleankod.exchange.entrypoint.service;

import pl.cleankod.exchange.core.domain.Account;

public interface AccountService {
    Account findAccountById(Account.Id id, String targetCurrency);
    Account findAccountByNumber(Account.Number number, String targetCurrency);
}
