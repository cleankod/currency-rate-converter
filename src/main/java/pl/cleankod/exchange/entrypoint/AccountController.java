package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Controller", description = "Operations related to managing bank accounts. Provides currency conversion and account details")
public class AccountController {
		
		private final AccountService accountService;
		
		public AccountController(AccountService accountService) {
				this.accountService = accountService;
		}
		
		@GetMapping(path = "/{id}")
		@Operation(summary = "Find an account by ID", description = "Provide an account ID to look up a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                     content = {@Content(mediaType = "application/json",
                                         schema = @Schema(implementation = Account.class))}),
        @ApiResponse(responseCode = "404", description = "The account with the given ID was not found")
    })
		public ResponseEntity<Account> findAccountById(
		@Parameter(description = "Account ID to find the account") @PathVariable String id,
		@Parameter(description = "Currency code to filter the account details")@RequestParam(required = false) String currency) {
				return accountService.findAccountById(id, Optional.ofNullable(currency))
								.map(ResponseEntity::ok)
								.orElse(ResponseEntity.notFound().build());
		}
		
		@GetMapping(path = "/number={number}")
		@Operation(summary = "Find an account by number", description = "Provide an account number to look up a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                     content = {@Content(mediaType = "application/json",
                                         schema = @Schema(implementation = Account.class))}),
        @ApiResponse(responseCode = "404", description = "The account with the given number was not found")
		})
		public ResponseEntity<Account> findAccountByNumber(
		@Parameter(description = "Account number to find the account") @PathVariable String number,
		@Parameter(description = "Currency code to filter the account details") @RequestParam(required = false) String currency) {
				return accountService.findAccountByNumber(number, Optional.ofNullable(currency))
								.map(ResponseEntity::ok)
								.orElse(ResponseEntity.notFound().build());
		}
		
		
}