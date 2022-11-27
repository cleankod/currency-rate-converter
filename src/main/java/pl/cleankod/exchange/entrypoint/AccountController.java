package pl.cleankod.exchange.entrypoint;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.AccountDto;
import pl.cleankod.exchange.core.domain.FindAccountFailedReason;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.exception.ErrorResponse;
import pl.cleankod.util.domain.JsonWriter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/accounts", produces = APPLICATION_JSON_VALUE)
public class AccountController {

    private final FindAccountUseCase findAccountUseCase;
    private final JsonWriter writer;

    public AccountController(final FindAccountUseCase findAccountUseCase, final JsonWriter writer) {
        this.findAccountUseCase = findAccountUseCase;
        this.writer = writer;
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Retrieve account details with account id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 404, message = "Account does not exist", response = ErrorResponse.class)
    })
    public ResponseEntity<AccountDto> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        final var account = findAccountUseCase.execute(Account.Id.of(id), currency);
        return ResponseEntity.ok(account.toDto());
    }

    @GetMapping(path = "/number={number}")
    @ApiOperation(value = "Retrieve account details with account number")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Ok", response = AccountDto.class),
        @ApiResponse(code = 404, message = "Account does not exist", response = FindAccountFailedReason.class)
    })
    public Object findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency, HttpServletResponse resp) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return findAccountUseCase.execute(accountNumber, currency)
            .fold(account -> writer.mapObjectToString(account.toDto()),
                findAccountFailedReason -> {
                resp.setStatus(404);
                return findAccountFailedReason;
                });
    }
}
