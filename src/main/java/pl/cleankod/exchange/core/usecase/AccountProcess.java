package pl.cleankod.exchange.core.usecase;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.UnknownCurrencyException;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Optional;

public class AccountProcess {
    private final Logger LOG = LoggerFactory.getLogger(AccountProcess.class);

    private final AccountRepository accountRepository;
    private final CurrencyConversionServiceSelector currencyConversionServiceSelector;

    public AccountProcess(AccountRepository accountRepository,
                          CurrencyConversionServiceSelector currencyConversionServiceSelector) {
        this.accountRepository = accountRepository;
        this.currencyConversionServiceSelector =  currencyConversionServiceSelector;
    }

    public Optional<Account> findAccount(Account.Id accountId, String currency) {
        Optional<Account> account = accountRepository.find(accountId);
        return account.map(acc -> convertAccount(acc, currency));
    }

    public Optional<Account> findAccount(Account.Number accountNumber, String currency) {
        Optional<Account> account = accountRepository.find(accountNumber);
        return account.map(acc -> convertAccount(acc, currency));
    }

    private Account convertAccount(Account account, String currency) {
        if (StringUtils.isBlank(currency)) {
            return account;
        }

        Currency targetCurrency = getMappedCurrency(currency);
        if (targetCurrency.equals(account.balance().currency())) {
            return account;
        }

        return new Account(account.id(), account.number(), convertToCurrency(account.balance(), targetCurrency));
    }

    private Money convertToCurrency(Money money, Currency targetCurrency) {
        CurrencyConversionService currencyConversionService = currencyConversionServiceSelector.selectService(money.currency());
        return currencyConversionService.convert(money, targetCurrency);
    }

    private Currency getMappedCurrency(String currency) {
        try {
            return Currency.getInstance(currency);
        } catch (Exception e) {
            LOG.warn("Unable to convert account. Unknown currency={}", currency);
            throw new UnknownCurrencyException("Unknown currency=" + currency);
        }
    }

}
