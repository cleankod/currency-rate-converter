package pl.cleankod.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD));
    }

}
