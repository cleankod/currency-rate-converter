package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.mapper.AccountMapper;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.model.AccountDto;

import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    private final AccountMapper accountMapper;

    public AccountServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                              FindAccountUseCase findAccountUseCase,
                              AccountMapper accountMapper) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
        this.accountMapper = accountMapper;
    }

    public AccountDto findAccountById(Account.Id accountId, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountId, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountId)

                )
                .map(accountMapper::accountToAccountDto)
                .orElseThrow(AccountNotFoundException::new);
    }

    public AccountDto findAccountByNumber(Account.Number accountNumber, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                )
                .map(accountMapper::accountToAccountDto)
                .orElseThrow(AccountNotFoundException::new);
    }
}
