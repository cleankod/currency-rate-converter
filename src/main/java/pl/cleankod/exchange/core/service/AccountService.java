package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.UnavailableExchangeRateException;
import pl.cleankod.exchange.core.port.AccountRepositoryPort;
import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.Result;

import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    private final AccountRepositoryPort accountRepositoryAdapter;

    public AccountService(CurrencyConversionService currencyConversionService, Currency baseCurrency, AccountRepositoryPort accountRepositoryAdapter) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
        this.accountRepositoryAdapter = accountRepositoryAdapter;
    }

    public Optional<Account> get(Account.Id id) {
        return accountRepositoryAdapter.find(id);
    }

    public Optional<Account> get(Account.Number number) {
        return accountRepositoryAdapter.find(number);
    }

    public Optional<Account> getById(String id, Optional<String> targetCurrency) {
        return get(Account.Id.of(id))
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );

    }

    public Optional<Account> getByNumber(String number, Optional<String> targetCurrency) {
        return get(Account.Number.of(number))
                .map(account -> targetCurrency
                        .map(currency -> new Account(account.id(), account.number(),
                                convert(account.balance(), Currency.getInstance(currency))))
                        .orElse(account)
                );
    }

    private Money convert(Money money, Currency targetCurrency) {

        // this solves a bug because without this clause here and in the case where
        // money.currency() = EUR and targetCurrency = EUR and  baseCurrency = PLN (hence != targetCurrency = EUR)
        // the original euros get wrongly converted to PLN
        if (money.currency().equals(targetCurrency)){
            return money;
        }

        if (!baseCurrency.equals(targetCurrency)) {
            Result<Money, Failure> result = currencyConversionService.convert(money, targetCurrency);
                if(result.isSuccessful()){
                    return result.successfulValue();
                }
                else { // can be customized, for instance, to return the account balance with the original currency
                       // and an additional end-user message stating that the conversion couldn't be performed
                    throw new UnavailableExchangeRateException(money.currency(), targetCurrency);
                }
        }

        // Case of money.currency() !=  targetCurrency = baseCurrency
        throw new CurrencyConversionException(money.currency(), targetCurrency);

    }
}
