package pl.cleankod.util.domain;

import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public final class ResponseEntityFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseEntityFactory.class);

    private ResponseEntityFactory() {
    }
    public static <T> ResponseEntity ok(T body) {
        LOGGER.debug("Returning OK response with body: {}", body);
        return ResponseEntity.status(OK).body(body);
    }
}