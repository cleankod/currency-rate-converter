package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.provider.FindAccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
@OpenAPIDefinition(info = @Info(title = "Accounts API", version = "1.0", description = "API for accessing to the customer account"))
@ApiResponses({
        @ApiResponse(description = "Successfully returned account object", responseCode = "200"),
        @ApiResponse(
                description = "Client sent an invalid request",
                responseCode = "400",
                content = @Content(examples = @ExampleObject(value = "{\"message\":\"Client sent an invalid request\"}"))
        ),
        @ApiResponse(
                description = "Server failed to fulfill a valid request due to an error with server",
                responseCode = "500",
                content = @Content(examples = @ExampleObject(value = "{\"message\":\"Server failed to fulfill a valid request due to an error with server\"}"))
        )
})
public class AccountController {

    private final FindAccountService findAccountService;

    public AccountController(FindAccountService findAccountService) {
        this.findAccountService = findAccountService;
    }

    @GetMapping(path = "/{id}")
    @Parameters({
            @Parameter(name = "id", description = "Account id"),
            @Parameter(name = "currency", description = "Account currency")
    })
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return findAccountService.findAccountById(Account.Id.of(URLDecoder.decode(id, StandardCharsets.UTF_8)), currency)
                .fold(
                        ResponseEntity::ok,
                        authenticationFailedReason -> ResponseEntity.notFound().build()
                );
    }

    @GetMapping(path = "/number={number}")
    @Parameters({
            @Parameter(name = "number", description = "Account number"),
            @Parameter(name = "currency", description = "Account currency")
    })
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return findAccountService.findAccountByNumber(Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8)), currency)
                .fold(
                        ResponseEntity::ok,
                        authenticationFailedReason -> ResponseEntity.notFound().build()
                );
    }


}
