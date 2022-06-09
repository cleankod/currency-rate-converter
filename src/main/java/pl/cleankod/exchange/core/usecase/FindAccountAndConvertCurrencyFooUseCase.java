package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.AccountDto;
import pl.cleankod.exchange.entrypoint.model.MoneyDto;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyFooUseCase {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public FindAccountAndConvertCurrencyFooUseCase(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<AccountDto> findAccountById(String currency, String id) {
        return Optional.ofNullable(currency)
                .map(fulfilledCurrency -> findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(fulfilledCurrency)))
                .orElseGet(() -> findAccountUseCase.execute(Account.Id.of(id)))
                .map(account -> new AccountDto(account.id().value(), account.number().value(), new MoneyDto(account.balance().amount(), account.balance().currency())));
    }

    public Optional<AccountDto> findAccountByAccountNumber(String currency, Account.Number accountNumber) {
        return Optional.ofNullable(currency)
                .map(fulfilledCurrency -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(fulfilledCurrency)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber))
                .map(account -> new AccountDto(account.id().value(), account.number().value(), new MoneyDto(account.balance().amount(), account.balance().currency())));
    }
}
