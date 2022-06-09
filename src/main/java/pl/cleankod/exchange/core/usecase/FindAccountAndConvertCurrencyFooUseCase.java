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

    public Optional<AccountDto> findAccountById(Currency currency, String id) {
        if (currency != null) {
            return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), currency).map(this::toAccountDto);
        }
        return findAccountUseCase.execute(Account.Id.of(id)).map(this::toAccountDto);
    }

    public Optional<AccountDto> findAccountByAccountNumber(Currency currency, Account.Number accountNumber) {
        if (currency != null) {
            return findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency).map(this::toAccountDto);
        }
        return findAccountUseCase.execute(accountNumber).map(this::toAccountDto);
    }

    private AccountDto toAccountDto(Account account) {
        return new AccountDto(account.id().value(), account.number().value(), new MoneyDto(account.balance().amount(), account.balance().currency()));
    }
}
