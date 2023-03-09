package pl.cleankod.exchange.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {

    PLN, EUR, THB, USD, AUD, HKD, CAD, NZD, SGD, HUF, GBP, UAH, JPY, CZK, DKK, ISK, NOK, SEK, RON, BGN, TRY, ILS, CLP, PHP, MXN, ZAR, BRL, MYR, IDR, INR, KRW, CNY, XDR;

    @JsonCreator
    public static Currency fromString(String currency) {
        return valueOf(currency.toUpperCase());
    }


    @JsonValue
    public String getValue() {
        return this.name();
    }

}
