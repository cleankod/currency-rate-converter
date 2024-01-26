package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Controller", description = "Controller for managing bank accounts. It provides operations for retrieving account details and performing currency conversions.")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping(path = "/{id}")
	@Operation(summary = "Find Account by ID", description = "Retrieve an account using its unique ID.")
	@ApiResponse(responseCode = "200", description = "Account found",
			content = @Content(schema = @Schema(implementation = Account.class)))
	@ApiResponse(responseCode = "404", description = "Account not found")
	public ResponseEntity<Account> findAccountById(
			@Parameter(description = "Unique ID of the account") @PathVariable String id,
			@Parameter(description = "Optional currency code to convert account balance") @RequestParam(required = false) String currency
	) {
		return accountService.findAccount(id, Optional.ofNullable(currency))
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// what is this ={number} ??
	@GetMapping(path = "/number={number}")
	@Operation(summary = "Find Account by Number", description = "Retrieve an account using its account number.")
	@ApiResponse(responseCode = "200", description = "Account found",
			content = @Content(schema = @Schema(implementation = Account.class)))
	@ApiResponse(responseCode = "404", description = "Account not found")
	public ResponseEntity<Account> findAccountByNumber(
			@Parameter(description = "Account number") @PathVariable String number,
			@Parameter(description = "Optional currency code to convert account balance") @RequestParam(required = false) String currency
	) {
		return accountService.findAccountByNumber(number, Optional.ofNullable(currency))
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}


}
