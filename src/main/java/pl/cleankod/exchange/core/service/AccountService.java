package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountDto;



public interface AccountService {

    AccountDto findAccountById(Account.Id accountId, String currency);

    AccountDto findAccountByNumber(Account.Number accountNumber, String currency);
}
