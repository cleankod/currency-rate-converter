package pl.cleankod.exchange.entrypoint.cache;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.annotation.Internal;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Introduction
@Type(NbpCacheInterceptor.class)
@Internal
public @interface NbpCacheAdvice {
}
