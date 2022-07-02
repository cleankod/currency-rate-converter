package pl.cleankod.exchange.core.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.MoneyOperationFailedReason;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.domain.Result;

import java.util.Currency;
import java.util.Optional;

public class ConvertAccountCurrencyServiceImpl implements ConvertAccountCurrencyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertAccountCurrencyServiceImpl.class);
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public ConvertAccountCurrencyServiceImpl(CurrencyConversionService currencyConversionService, Currency baseCurrency) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public Result<Account, MoneyOperationFailedReason> execute(Account account, Currency targetCurrency) {
        LOGGER.info("Starting to convert account with id {} to target currency of {}", account.id().value(), targetCurrency.getCurrencyCode());
        return convert(account.balance(), targetCurrency)
                .map(money -> Result.<Account, MoneyOperationFailedReason>successful(new Account(account.id(), account.number(), money)))
                .orElseGet(() -> Result.fail(MoneyOperationFailedReason.conversionFailed(account.balance().currency(), targetCurrency)));
    }

    private Optional<Money> convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return Optional.ofNullable(money.convert(currencyConversionService, targetCurrency));
        }
        LOGGER.info("Money are already in target currency of {}", targetCurrency.getCurrencyCode());
        return Optional.of(money);
    }
}
