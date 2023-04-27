package pl.cleankod.exchange.core.usecase;

import lombok.extern.slf4j.Slf4j;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.CurrencyConversionException;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.exception.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.ErrorResponse;
import pl.cleankod.exchange.entrypoint.util.Result;

import java.util.Currency;

@Slf4j
public class FindAccountAndConvertCurrencyUseCase {

    private static final String CANNOT_CONVERT_CURRENCY_EXCEPTION_MESSAGE = "Cannot convert currency from %s to %s.";
    private static final String SUCCESSFULLY_CONVERT_CURRENCY_MESSAGE = "Successfully converted currency from %s to %s.";

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

    public Account findAccountById(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)))
                .orElseThrow(() -> new AccountNotFoundException(id.toString()));

    }

    public Account findAccountByNumber(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account -> new Account(account.id(), account.number(), convert(account.balance(), targetCurrency)))
                .orElseThrow(() -> new AccountNotFoundException(number.toString()));
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            Result<Money, ErrorResponse> conversionResult = currencyConversionService.convert(money, targetCurrency);
            if(conversionResult.isSuccessful()) {
                log.info(String.format(SUCCESSFULLY_CONVERT_CURRENCY_MESSAGE, money.currency(), targetCurrency));
                return conversionResult.successfulValue();
            } else {
                log.error(conversionResult.failValue().toString());
                throw new CurrencyConversionException(money.currency(), targetCurrency);

            }
        }

        if (!money.currency().equals(targetCurrency)) {
            log.error(String.format(CANNOT_CONVERT_CURRENCY_EXCEPTION_MESSAGE, money.currency(), targetCurrency));
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
