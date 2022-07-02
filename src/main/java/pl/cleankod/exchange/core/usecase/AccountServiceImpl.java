package pl.cleankod.exchange.core.usecase;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountOperationFailedReason;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.util.domain.Result;

import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final ConvertAccountCurrencyService convertAccountCurrencyService;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            ConvertAccountCurrencyService convertAccountCurrencyService) {
        this.accountRepository = accountRepository;
        this.convertAccountCurrencyService = convertAccountCurrencyService;
    }

    @Override
    public Result<Account, AccountOperationFailedReason> getByIdWithDesiredCurrency(Account.Id id, String currency) {
        LOGGER.info("Getting account with id {} and with currency converted to {}", id.value(), currency);
        var findAccountResult = accountRepository.find(id);
        if (findAccountResult.isFail()) {
            LOGGER.warn("Account with id {} was not found.", id.value());
            return findAccountResult;
        }
        return convertCurrencyForAccount(findAccountResult, currency);
    }

    @Override
    public Result<Account, AccountOperationFailedReason> getByNumberWithDesiredCurrency(Account.Number number, String currency) {
        LOGGER.info("Getting account with number {} and with currency converted to {}", number.value(), currency);
        var findAccountResult = accountRepository.find(number);
        if (findAccountResult.isFail()) {
            LOGGER.warn("Account with number {} was not found.", number.value());
            return findAccountResult;
        }
        return convertCurrencyForAccount(findAccountResult, currency);
    }

    private Result<Account, AccountOperationFailedReason> convertCurrencyForAccount(
            Result<Account, AccountOperationFailedReason> findAccountResult,
            String currency) {
        return Optional.ofNullable(currency)
                .map(c -> convertAccountCurrencyService
                        .execute(findAccountResult.successfulValue(), Currency.getInstance(c))
                        .failMap(reason -> new AccountOperationFailedReason(reason.getMessage(), HttpStatus.SC_BAD_REQUEST)))
                .orElse(findAccountResult);
    }
}
