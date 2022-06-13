package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.rate.MidRate;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;
import java.util.Optional;

public class FindAccountAndConvertCurrencyUseCase {

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

    public Optional<Account> execute(Account.Id id, Currency targetCurrency) {
        return accountRepository.find(id)
                .map(account ->
                        {
                            MidRate midRate = findMidRateFor(targetCurrency);
                            return new Account(
                                    account.id(),
                                    account.number(),
                                    account.balance().convert(baseCurrency, midRate.currency(), midRate.rate())
                            );
                        }
                );
    }

    public Optional<Account> execute(Account.Number number, Currency targetCurrency) {
        return accountRepository.find(number)
                .map(account ->
                        {
                            MidRate midRate = findMidRateFor(targetCurrency);
                            return new Account(
                                    account.id(),
                                    account.number(),
                                    account.balance().convert(baseCurrency, midRate.currency(), midRate.rate())
                            );
                        }
                );
    }

    private MidRate findMidRateFor(Currency targetCurrency) {
        RateWrapper.MidRate midRate = currencyConversionService.getMidRate(targetCurrency);
        return new MidRate(midRate.currency(), midRate.rate());
    }
}
