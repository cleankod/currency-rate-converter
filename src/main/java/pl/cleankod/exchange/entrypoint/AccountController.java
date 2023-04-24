package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Account> findAccountById(String id, String currency) {
        return accountService.findAccountById(id, currency);
    }

    @Override
    public ResponseEntity<Account> findAccountByNumber(String number, String currency) {
        return accountService.findAccountByNumber(number, currency);
    }

}
