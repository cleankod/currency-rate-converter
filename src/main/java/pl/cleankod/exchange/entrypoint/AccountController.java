package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyFooUseCase;
import pl.cleankod.exchange.entrypoint.model.AccountDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyFooUseCase findAccountAndConvertCurrencyFooUseCase;

    public AccountController(FindAccountAndConvertCurrencyFooUseCase findAccountAndConvertCurrencyFooUseCase) {
        this.findAccountAndConvertCurrencyFooUseCase = findAccountAndConvertCurrencyFooUseCase;
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<AccountDto> findAccountById(@PathVariable String id, @RequestParam(required = false) Currency currency) {
        return findAccountAndConvertCurrencyFooUseCase.findAccountById(currency, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    ResponseEntity<AccountDto> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) Currency currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));

        return findAccountAndConvertCurrencyFooUseCase.findAccountByAccountNumber(currency, accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
