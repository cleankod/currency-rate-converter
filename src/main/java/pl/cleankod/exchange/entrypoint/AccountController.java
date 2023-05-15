package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.service.AccountService;
import pl.cleankod.util.ErrorTransformer;
import pl.cleankod.util.domain.Currencies;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

@RestController
@RequestMapping("/accounts")
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<?> findAccountById(String id, String currency) {
        Account.Id accountId = Account.Id.of(id);
        Currency targetCurrency = Currencies.getOrNull(currency);

        return accountService.find(accountId, targetCurrency)
                .fold(ResponseEntity::ok, ErrorTransformer::toApiError);
    }

    @Override
    public ResponseEntity<?> findAccountByNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        Currency targetCurrency = Currencies.getOrNull(currency);

        return accountService.find(accountNumber, targetCurrency)
                .fold(ResponseEntity::ok, ErrorTransformer::toApiError);
    }
}
