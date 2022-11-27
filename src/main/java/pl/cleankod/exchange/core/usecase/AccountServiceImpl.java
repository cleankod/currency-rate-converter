package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.dto.AccountDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<AccountDto> getAccountDtoById(String id, String currency) {
        var accountId = Account.Id.of(URLDecoder.decode(id, StandardCharsets.UTF_8));

        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountId, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountId)
                )
                .map(AccountDto::from);
    }

    public Optional<AccountDto> getAccountDtoByNumber(String number, String currency) {
        var accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));

        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                )
                .map(AccountDto::from);
    }

}
