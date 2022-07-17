
package pl.cleankod.exchange.core.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public AccountServiceImpl(AccountRepository accountRepository, CurrencyConversionService currencyConversionService, Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public AccountResponse findAccountBy(Account.Id id, String targetCurrency, String correlationId) throws AccountNotFoundException {

        return accountRepository.find(id)
                .map(account -> {
                    Money money = getMoney(targetCurrency, account, correlationId);
                    return this.buildAccountDtoFrom(account, money);
                })
                .orElseThrow(() -> new AccountNotFoundException(correlationId));

    }

    @Override
    public AccountResponse findAccountBy(Account.Number number, String targetCurrency, String correlationId) throws AccountNotFoundException {

        return accountRepository.find(number)
                .map(account -> {
                    log.debug("Account found with number {} and with targetCurrency {} for CID: {}", number, targetCurrency, correlationId);
                    Money money = getMoney(targetCurrency, account, correlationId);
                    return this.buildAccountDtoFrom(account, money);
                })
                .orElseThrow(() -> new AccountNotFoundException(correlationId));
    }

    private Money getMoney(String targetCurrency, Account account, String correlationId) {
        return Optional.ofNullable(targetCurrency).map(money -> this.convert(account.balance(), Currency.getInstance(targetCurrency.toUpperCase()), correlationId))
                .orElse(account.balance());
    }

    private Money convert(Money money, Currency targetCurrency, String correlationId) {

        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency, correlationId);
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
                                money.currency().getCurrencyCode()));
    }

}