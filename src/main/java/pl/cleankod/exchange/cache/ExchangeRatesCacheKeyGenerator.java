package pl.cleankod.exchange.cache;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ExchangeRatesCacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return String.format("%s:%s:%s", target.getClass().getName(), method.getName(), Arrays.hashCode(params));
    }
}