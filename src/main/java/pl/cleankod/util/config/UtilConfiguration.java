package pl.cleankod.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cleankod.util.domain.JsonWriter;

@Configuration
public class UtilConfiguration {

    @Bean
    public JsonWriter jsonWriter(ObjectMapper mapper) {
        return new JsonWriter(mapper);
    }
}
