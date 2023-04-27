package pl.cleankod.exchange.entrypoint.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }


    @Override
    public Account findAccountById(Account.Id id, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.findAccountById(id, Currency.getInstance(s)))
                .orElseGet(() ->
                        findAccountUseCase.findAccountById(id)
                );

    }

    @Override
    public Account findAccountByNumber(Account.Number accountNumber, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.findAccountByNumber(accountNumber, Currency.getInstance(s)))
                .orElseGet(() ->
                        findAccountUseCase.findAccountByNumber(accountNumber)
                );
    }
}
