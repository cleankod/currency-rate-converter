package pl.cleankod.exchange.entrypoint;

import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.CurrencyConverter;
import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CurrencyConverter currencyConverter = new CurrencyConverter() {
        private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(4.58d);
        private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(0.22d);
        @Override
        public Money convert(Money money, Currency targetCurrency) {
            return money.currency().equals(targetCurrency)
                    ? money
                    : calculate(money, targetCurrency);
        }

        private Money calculate(Money money, Currency targetCurrency) {
            BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
            return Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), targetCurrency);
        }
    };

    @GetMapping(path = "/{id}")
    public Account findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return new Account(
                Account.Id.of(id),
                Account.Number.of("65 1090 1665 0000 0001 0373 7343"),
                convert(currency, Money.of("123.45", "PLN"))
        );
    }

    @GetMapping(path = "/number={number}")
    public Account findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return new Account(
                Account.Id.of("78743420-8ce9-11ec-b0d0-57b77255c208"),
                Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8)),
                convert(currency, Money.of("456.78", "EUR"))
        );
    }

    private Money convert(String currency, Money money) {
        if (currency != null) {
            return money.convert(currencyConverter, Currency.getInstance(currency));
        }
        return money;
    }

}
