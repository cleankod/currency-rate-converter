package pl.cleankod.exchange.entrypoint.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public record AccountNumberDto(String number) {

    public String decode() {
        return URLDecoder.decode(number, StandardCharsets.UTF_8);
    }
}
