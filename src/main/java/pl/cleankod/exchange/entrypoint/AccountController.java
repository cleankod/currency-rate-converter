package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Currency;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Tag(name = "Currency Rate Converter", description = "Currency Rate Converter API")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }
    @Operation(summary = "Find account by id", description = "Returns a single account with default or chosen currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content) })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) Currency currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), currency)

                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(Account.Id.of(id))
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                );
    }
    @Operation(summary = "Find account by number", description = "Returns a single account with default or chosen currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "account not found", content = @Content) })
    @GetMapping(path = "/number={number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) Currency currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                );
    }


}
