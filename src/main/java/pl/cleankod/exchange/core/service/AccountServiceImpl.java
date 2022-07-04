package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.service.mapper.AccountMapper;
import pl.cleankod.exchange.entrypoint.model.AccountDto;

import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;


    public AccountServiceImpl(AccountMapper accountMapper,
                              CurrencyConversionService currencyConversionService,
                              AccountRepository accountRepository,
                              Currency baseCurrency) {
        this.accountMapper = accountMapper;
        this.currencyConversionService = currencyConversionService;
        this.accountRepository = accountRepository;
        this.baseCurrency = baseCurrency;
    }

    public AccountDto findAccountById(Account.Id accountId, String currency) {
        return accountRepository.find(accountId)
                .map(account -> convertCurrencyIfNeeded(account, currency))
                .map(accountMapper::accountToAccountDto)
                .orElseThrow(AccountNotFoundException::new);
    }

    public AccountDto findAccountByNumber(Account.Number accountNumber, String currency) {
        return accountRepository.find(accountNumber)
                .map(account -> convertCurrencyIfNeeded(account, currency))
                .map(accountMapper::accountToAccountDto)
                .orElseThrow(AccountNotFoundException::new);
    }

    private Account convertCurrencyIfNeeded (Account account, String currency) {
        return Optional.ofNullable(currency)
                .map(s -> new Account(account.id(), account.number(), convert(account.balance(), Currency.getInstance(s))))
                .orElse(account);
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }
        return money;
    }
}
