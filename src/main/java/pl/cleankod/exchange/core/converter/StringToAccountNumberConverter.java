package pl.cleankod.exchange.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.cleankod.exchange.core.domain.Account;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class StringToAccountNumberConverter implements Converter<String, Account.Number> {

    @Override
    public Account.Number convert(String source) {
        return Account.Number.of(URLDecoder.decode(source, StandardCharsets.UTF_8));
    }

}
