package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.exception.AccountNotFoundException;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountService {
    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Account findAccountByIdAndCurrency(String id, String currency) {
        return Optional.ofNullable(currency)
                .map(givenCurrency ->
                        findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(givenCurrency))
                                .orElseThrow(() -> new AccountNotFoundException(String.format("Account not found for id %s and currency %s", id, currency))))
                .orElseGet(() ->
                        findAccountUseCase.execute(Account.Id.of(id))
                                .orElseThrow(() -> new AccountNotFoundException(String.format("Account not found for id %s", id))));
    }

    public Account findAccountByNumberAndCurrency(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(givenCurrency ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(givenCurrency))
                                .orElseThrow(() -> new AccountNotFoundException(String.format("Account not found for number %s and currency %s", number, currency))))
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                                .orElseThrow(() -> new AccountNotFoundException(String.format("Account not found for number %s", number))));
    }
}
