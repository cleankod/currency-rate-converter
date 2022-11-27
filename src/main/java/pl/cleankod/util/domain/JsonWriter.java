package pl.cleankod.util.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.cleankod.exchange.entrypoint.exception.CurrencyRateConverterException;

@Slf4j
public record JsonWriter(ObjectMapper mapper) {

    public String mapObjectToString(final Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to write object as string", e);
            throw new CurrencyRateConverterException(CurrencyRateConverterException.UNKNOWN_ERROR_MESSAGE);
        }
    }
}
