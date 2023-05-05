package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import jakarta.inject.Singleton;

@Factory
public class ObjectMapperFactory {
    @Bean
    @Singleton
    @Replaces(ObjectMapper.class)
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new CustomAnnotationIntrospector());
        return objectMapper;
    }
}
