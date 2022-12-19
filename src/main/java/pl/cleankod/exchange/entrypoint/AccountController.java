package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
public class AccountController implements AccountApi {

    private final FindAccountService findAccountService;

    public AccountController(FindAccountService findAccountService) {
        this.findAccountService = findAccountService;
    }

    public ResponseEntity<Account> findAccountById(String id, String currency) {
        return findAccountService.find(Account.Id.of(id), currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Account> findAccountByNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return findAccountService.find(accountNumber, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
