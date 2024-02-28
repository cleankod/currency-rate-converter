package pl.cleankod.exchange.core.usecase;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public AccountService(AccountRepository accountRepository,
                          CurrencyConversionService currencyConversionService,
                          Currency baseCurrency) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public ResponseEntity<Account> executeConversionByAccountId(String id, String currency) {
        Account.Id accountId = Account.Id.of(id);
        Optional<Account> account = accountRepository.find(accountId);

        return execute(account, currency);
    }

    private ResponseEntity<Account> execute(Optional<Account> account, String currency) {

        if(currency != null) {
            return account
                    .map(item -> executeConversion(item, Currency.getInstance(currency)))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        return account
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Account> executeConversionByAccountNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        Optional<Account> account = accountRepository.find(accountNumber);

        return execute(account, currency);
    }

    private Account executeConversion(Account account, Currency currency) {
        return  new Account(account.id(), account.number(), convert(account.balance(), currency));
    }

    private Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return currencyConversionService.convert(money, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
