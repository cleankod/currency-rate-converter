package pl.cleankod.util;

import org.springframework.lang.NonNull;

import java.time.LocalDate;

public  record CurrencyConversionUtils() {
    public static LocalDate getPreviousWorkingDay(@NonNull LocalDate localDate) {
        LocalDate dateOfExchange;
        switch (localDate.getDayOfWeek()) {
            case MONDAY -> dateOfExchange = localDate.minusDays(3);
            case SUNDAY -> dateOfExchange = localDate.minusDays(2);
            default -> dateOfExchange = localDate.minusDays(1);
        }
        return dateOfExchange;
    }
}
