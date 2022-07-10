
package pl.cleankod.exchange.core.usecase;

import org.springframework.stereotype.Service;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.AccountService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.AccountResponse;
import pl.cleankod.exchange.entrypoint.model.CurrencyConversionException;

import java.util.Currency;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public AccountServiceImpl(AccountRepository accountRepository, CurrencyConversionService currencyConversionService, Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public AccountResponse findAccountBy(Account.Id id, String targetCurrency) throws AccountNotFoundException {

        return accountRepository.find(id)
                .map(account -> {
                    Money money = getMoney(targetCurrency, account);
                    return this.buildAccountDtoFrom(account, money);
                })
                .orElseThrow(() -> new AccountNotFoundException(AccountNotFoundException.ACCOUNT_NOT_FOUND));

    }

    @Override
    public AccountResponse findAccountBy(Account.Number number, String targetCurrency) throws AccountNotFoundException {
        return accountRepository.find(number)
                .map(account -> {
                    Money money = getMoney(targetCurrency, account);
                    return this.buildAccountDtoFrom(account, money);
                })
                .orElseThrow(() -> new AccountNotFoundException(AccountNotFoundException.ACCOUNT_NOT_FOUND));
    }

    private Money getMoney(String targetCurrency, Account account) {
        Money money = null;
        if (targetCurrency != null) {
            money = this.convert(account.balance(), Currency.getInstance(targetCurrency));
        } else {
            money = account.balance();
        }
        return money;
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

    private AccountResponse buildAccountDtoFrom(Account account, Money money) {

        return
                new AccountResponse(
                        account.id().value().toString(),
                        account.number().value(),
                        new AccountResponse.MoneyResponse(
                                money.amount(),
                                money.currency()));
    }

}