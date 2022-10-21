package pl.cleankod.exchange.provider.nbp;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NPBCache {

    private static int CACHE_SIZE = 3;
    private static Map<Currency, RateWrapperObject> cache = new ConcurrentHashMap<>(CACHE_SIZE);

    public void put(Currency currency, RateWrapper rateWrapper) {
        if (cache.size() == CACHE_SIZE) {
            cache.entrySet().remove(findMostUnusedKey());
        }
        cache.put(currency, new RateWrapperObject(1, rateWrapper));
    }

    private Map.Entry<Currency, RateWrapperObject> findMostUnusedKey() {
        Iterator<Map.Entry<Currency, RateWrapperObject>> iterator  = cache.entrySet().iterator();
        Map.Entry<Currency, RateWrapperObject> mostUnused = iterator.next();
        Map.Entry<Currency, RateWrapperObject> nextHit;

        while (iterator.hasNext()) {
            nextHit = iterator.next();

            if (mostUnused.getValue().getUsageTimes() > nextHit.getValue().getUsageTimes()) {
                mostUnused = nextHit;
            }
        }

        return mostUnused;
    }

    public RateWrapper get(Currency currency) {
       RateWrapperObject rateWrapperObject = cache.get(currency);

       if (Objects.isNull(rateWrapperObject)) {
           return null;
       }

       rateWrapperObject.setUsageTimes(rateWrapperObject.getUsageTimes() + 1);
       return rateWrapperObject.getRateWrapper();
    }

    private class RateWrapperObject {
        private int usageTimes;
        private RateWrapper rateWrapper;

        public RateWrapperObject(int usageTimes, RateWrapper rateWrapper) {
            this.usageTimes = usageTimes;
            this.rateWrapper = rateWrapper;
        }

        public void setUsageTimes(int usageTimes) {
            this.usageTimes = usageTimes;
        }

        public int getUsageTimes() {
            return usageTimes;
        }

        public RateWrapper getRateWrapper() {
            return rateWrapper;
        }
    }
}
