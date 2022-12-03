package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final ConvertMoneyByCurrencyUseCase convertMoneyByCurrencyUseCase;


    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                ConvertMoneyByCurrencyUseCase convertMoneyByCurrencyUseCase) {
        this.accountRepository = accountRepository;
        this.convertMoneyByCurrencyUseCase = convertMoneyByCurrencyUseCase;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> new Account(
                        account.id(),
                        account.number(),
                        convertMoneyByCurrencyUseCase.convert(account.balance(), targetCurrency)));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> new Account(
                        account.id(),
                        account.number(),
                        convertMoneyByCurrencyUseCase.convert(account.balance(), targetCurrency)));
    }
}
