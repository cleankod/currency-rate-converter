package pl.cleankod.exchange.core.usecase;

import java.util.Currency;

import org.apache.commons.lang3.StringUtils;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.FindAccountFailedReason;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.entrypoint.exception.NotFoundException;
import pl.cleankod.util.domain.Result;

public class FindAccountUseCase {

    private final AccountRepository accountRepository;
    private final ConvertCurrencyUseCase convertCurrencyUseCase;

    public FindAccountUseCase(final AccountRepository accountRepository, final ConvertCurrencyUseCase convertCurrencyUseCase) {
        this.accountRepository = accountRepository;
        this.convertCurrencyUseCase = convertCurrencyUseCase;
    }

    public Account execute(Account.Id id, String targetCurrency) {
        final var existingAccount = accountRepository.find(id)
            .orElseThrow(() -> new NotFoundException("Account does not exist"));

        return convertCurrencyIfRequested(targetCurrency, existingAccount);
    }

    public Result<Account, FindAccountFailedReason> execute(Account.Number number, String targetCurrency) {
        return accountRepository.find(number)
            .map(account -> convertCurrencyIfRequested(targetCurrency, account))
            .map(Result::<Account, FindAccountFailedReason>successful)
            .orElseGet(() -> Result.fail(FindAccountFailedReason.ACCOUNT_DOES_NOT_EXIST));
    }

    private Account convertCurrencyIfRequested(final String targetCurrency, final Account existingAccount) {
        if (StringUtils.isBlank(targetCurrency)) {
            return existingAccount;
        } else {
            return new Account(existingAccount.id(), existingAccount.number(),
                convertCurrencyUseCase.convert(existingAccount.balance(), Currency.getInstance(targetCurrency)));
        }
    }
}
