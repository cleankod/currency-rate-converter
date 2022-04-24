package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository,
                                                CurrencyConversionService currencyConversionService,
                                                Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .flatMap(account -> convertAccountMoney(account, targetCurrency));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .flatMap(account -> convertAccountMoney(account, targetCurrency));
    }

    private Optional<Account> convertAccountMoney(Account account, Currency targetCurrency) {
        var money = account.balance();
        if (!baseCurrency.equals(targetCurrency)) {
            return currencyConversionService.convert(money, targetCurrency)
                    .map(m -> new Account(account.id(), account.number(), m))
                    .or(() -> Optional.of(new Account(account.id(), account.number())));
        }
        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }
        return Optional.of(account);
    }
}
