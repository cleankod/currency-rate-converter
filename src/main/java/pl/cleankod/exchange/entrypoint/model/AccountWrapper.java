package pl.cleankod.exchange.entrypoint.model;

import java.util.UUID;

public record AccountWrapper(UUID id, String number, MoneyWrapper balance){

}

