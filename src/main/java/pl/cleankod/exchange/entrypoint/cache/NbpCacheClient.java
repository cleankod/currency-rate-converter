package pl.cleankod.exchange.entrypoint.cache;

import jakarta.inject.Singleton;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

@Singleton
@NbpCacheAdvice
public interface NbpCacheClient extends ExchangeRatesNbpClient {
}
