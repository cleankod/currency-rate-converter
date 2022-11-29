package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.AccountProcess;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Account Controller")
public class AccountController {

    private final AccountProcess accountProcess;

    public AccountController(AccountProcess accountProcess) {
        this.accountProcess = accountProcess;
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "findAccountById", description = "Returns Account converted to specified currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Details"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No accounts were found", content = @Content),
            @ApiResponse(responseCode = "500", description = "System Error", content = @Content)
    })
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return accountProcess.findAccount(Account.Id.of(id), currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    @Operation(summary = "findAccountByNumber", description = "Returns Account converted to specified currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Details"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No accounts were found", content = @Content),
            @ApiResponse(responseCode = "500", description = "System Error", content = @Content)
    })
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return accountProcess.findAccount(accountNumber, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
