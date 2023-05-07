package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.WholeMoney;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.ExchangeRateService;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    private final AccountRepository accountRepository;
    private final ExchangeRateService exchangeRateService;

    public FindAccountAndConvertCurrencyUseCase(AccountRepository accountRepository, ExchangeRateService exchangeRateService) {
        this.accountRepository = accountRepository;
        this.exchangeRateService = exchangeRateService;
    }

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)));
    }

    private WholeMoney convert(WholeMoney money, Currency targetCurrency) {
        if (money.currency().equals(targetCurrency)) {
            return money;
        }

        return exchangeRateService
                .getExchangeRate(money.currency(), targetCurrency)
                .map(money::convert)
                .map(Money::roundToWhole)
                .orElseThrow(() -> new CurrencyConversionException(money.currency(), targetCurrency));
    }
}
