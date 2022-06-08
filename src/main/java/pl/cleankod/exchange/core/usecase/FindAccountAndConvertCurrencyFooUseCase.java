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
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(Account.Id.of(id)))
                .map(s -> new AccountDto(s.id().value(), s.number().value(), new MoneyDto(s.balance().amount(), s.balance().currency())));
    }

    public Optional<AccountDto> findAccountByAccountNumber(String currency, Account.Number accountNumber) {
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber))
                .map(s -> new AccountDto(s.id().value(), s.number().value(), new MoneyDto(s.balance().amount(), s.balance().currency())));
    }
}
