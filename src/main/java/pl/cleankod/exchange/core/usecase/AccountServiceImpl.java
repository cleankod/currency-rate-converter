package pl.cleankod.exchange.core.usecase;

import org.apache.http.HttpStatus;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountOperationFailedReason;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.util.domain.Result;

import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
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
        var findAccountResult = accountRepository.find(id);
        return convertCurrencyForAccount(findAccountResult, currency);
    }

    @Override
    public Result<Account, AccountOperationFailedReason> getByNumberWithDesiredCurrency(Account.Number number, String currency) {
        var findAccountResult = accountRepository.find(number);
        return convertCurrencyForAccount(findAccountResult, currency);
    }

    private Result<Account, AccountOperationFailedReason> convertCurrencyForAccount(
            Result<Account, AccountOperationFailedReason> findAccountResult,
            String currency) {
        if (findAccountResult.isFail()) {
            return findAccountResult;
        }
        return Optional.ofNullable(currency)
                .map(c -> convertAccountCurrencyService
                        .execute(findAccountResult.successfulValue(), Currency.getInstance(c))
                        .failMap(reason -> new AccountOperationFailedReason(reason.getMessage(), HttpStatus.SC_BAD_REQUEST)))
                .orElse(findAccountResult);
    }
}
