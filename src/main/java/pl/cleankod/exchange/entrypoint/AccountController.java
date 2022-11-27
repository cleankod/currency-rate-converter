package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.usecase.AccountService;
import pl.cleankod.exchange.core.usecase.dto.AccountDto;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountDto> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return accountService.getAccountDtoById(id, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<AccountDto> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return accountService.getAccountDtoByNumber(number, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
