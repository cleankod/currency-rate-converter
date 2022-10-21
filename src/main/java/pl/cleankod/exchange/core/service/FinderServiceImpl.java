package pl.cleankod.exchange.core.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDTO;
import pl.cleankod.exchange.core.gateway.FinderService;
import pl.cleankod.exchange.core.usecase.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

@Service
public class FinderServiceImpl implements FinderService {
    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public FinderServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }


    public ResponseEntity<AccountDTO> findAccountById(String id, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountByIdAndConvertToCurrency(id, s)
                )
                .orElseGet(() ->
                        findAccountById(id)
                );
    }

    public ResponseEntity<AccountDTO> findAccountByNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                                .map(ResponseEntity::ok).get()
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                                .map(ResponseEntity::ok).get()
                );
    }

    private ResponseEntity<AccountDTO> findAccountByIdAndConvertToCurrency(String id, String currency) throws UserNotFoundException {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), findCurrencyOrThrowError(currency))
                .map(ResponseEntity::ok).get();
    }

    private ResponseEntity<AccountDTO> findAccountById(String id) {
        return findAccountUseCase.execute(Account.Id.of(id))
                .map(ResponseEntity::ok).get();
    }

    private Currency findCurrencyOrThrowError(String currency) throws CurrencyConversionException {
        try {
            return Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new CurrencyNotFoundException(currency);
        }
    }
}
