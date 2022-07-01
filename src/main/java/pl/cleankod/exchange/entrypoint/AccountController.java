package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Account> findAccountById(String id, String currency) {
        return accountService.getByIdWithDesiredCurrency(Account.Id.of(id), currency)
                .fold(ResponseEntity::ok, operationFailedReason -> {
                    throw new AccountOperationException(operationFailedReason.getStatusCode(), operationFailedReason.getMessage());
                });
    }

    @Override
    public ResponseEntity<Account> findAccountByNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return accountService.getByNumberWithDesiredCurrency(accountNumber, currency)
                .fold(ResponseEntity::ok, operationFailedReason -> {
                    throw new AccountOperationException(operationFailedReason.getStatusCode(), operationFailedReason.getMessage());
                });
    }
}
