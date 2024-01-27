package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
		
		private final AccountService accountService;
		
		public AccountController(AccountService accountService) {
				this.accountService = accountService;
		}
		
		@GetMapping(path = "/{id}")
		public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
				return accountService.findAccountById(id, Optional.ofNullable(currency))
								.map(ResponseEntity::ok)
								.orElse(ResponseEntity.notFound().build());
		}
		
		@GetMapping(path = "/number={number}")
		public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
				return accountService.findAccountByNumber(number, Optional.ofNullable(currency))
								.map(ResponseEntity::ok)
								.orElse(ResponseEntity.notFound().build());
		}
		
		
}