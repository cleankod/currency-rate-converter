package pl.cleankod.exchange.entrypoint.cache;

import feign.httpclient.ApacheHttpClient;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.cache.SyncCache;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Arrays;

@Singleton
public class NbpCacheInterceptor implements MethodInterceptor<ExchangeRatesNbpClient, Object> {

    private final SyncCache<?> cache;
    private final ExchangeRatesNbpClient client;

    public NbpCacheInterceptor(@Named("nbp") SyncCache<?> cache,
                               @Property(name = "provider.nbp-api.base-url") String nbpApiBaseUrl) {
        this.cache = cache;
        client = exchangeRatesNbpClient(nbpApiBaseUrl);
    }

    @Override
    public Object intercept(MethodInvocationContext<ExchangeRatesNbpClient, Object> context) {
        var parameterValues = context.getParameterValues();
        return cache.get(Arrays.toString(parameterValues), Object.class,
                () -> context.getExecutableMethod().invoke(client, parameterValues));
    }

    private static ExchangeRatesNbpClient exchangeRatesNbpClient(String nbpApiBaseUrl) {
        FallbackFactory<ExchangeRatesNbpClient> fallbackFactory = cause -> (table, currency) -> null;
        return HystrixFeign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl, fallbackFactory);
    }

}
