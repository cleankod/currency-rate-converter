package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.domain.AppErrors;
import pl.cleankod.util.domain.Either;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

    public static final Either<AppErrors.NotFound, Account> ACCOUNT_NOT_FOUND = AppErrors.eitherNotFound("Account not found");

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

    public Either<? extends AppErrors.Base, Account> execute(Account.Id id, Currency targetCurrency) {
        Optional<Account> account = accountRepository.find(id);
        if (account.isEmpty()) {
            return ACCOUNT_NOT_FOUND;
        }
        return convertForAccount(account.get(), targetCurrency);
    }

    public Either<? extends AppErrors.Base, Account> execute(Account.Number number, Currency targetCurrency) {
        Optional<Account> account = accountRepository.find(number);
        if (account.isEmpty()) {
            return ACCOUNT_NOT_FOUND;
        }
        return convertForAccount(account.get(), targetCurrency);
    }

    private Either<? extends AppErrors.Base, Account> convertForAccount(Account account, Currency targetCurrency) {
        if (targetCurrency == null) {
            return Either.right(account);
        }
        Money balance = account.balance();
        if (!baseCurrency.equals(balance.currency())) {
            return AppErrors.eitherConversion(balance.currency(), targetCurrency);
        }
        return convertBalance(balance, targetCurrency)
                .mapRight(convertedBalance -> new Account(account.id(), account.number(), convertedBalance));
    }

    private Either<? extends AppErrors.Base, Money> convertBalance(Money balance, Currency targetCurrency) {
        return currencyConversionService.convert(balance, targetCurrency);

    }

}
