package pl.cleankod.exchange.entrypoint;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.util.Currency;
import java.util.Optional;

@Controller("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    @Get("/{id}")
    public HttpResponse<Account> findAccountById(@PathVariable Account.Id id, @QueryValue Optional<Currency> currency) {
        return currency
                .map(it -> findAccountAndConvertCurrencyUseCase.execute(id, it))
                .orElseGet(() -> findAccountUseCase.execute(id))
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Get("/number={+number}")
    public HttpResponse<Account> findAccountByNumber(@PathVariable Account.Number number, @QueryValue Optional<Currency> currency) {
        return currency
                .map(it -> findAccountAndConvertCurrencyUseCase.execute(number, it))
                .orElseGet(() -> findAccountUseCase.execute(number))
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }
}
