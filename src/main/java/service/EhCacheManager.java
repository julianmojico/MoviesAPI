package service;

import io.dropwizard.lifecycle.Managed;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.ArrayList;

@Singleton
public class EhCacheManager implements Managed {

    private ArrayList<String> cacheNames;
    private CacheManager cacheManager;

    public EhCacheManager() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .build();
        cacheManager.init();
        this.cacheNames = new ArrayList<String>();
    }

    public <K, V> Cache<K, V> createCache(String name, Class<K> keyClass, Class<V> valueClass, int heapSize, Duration timeToLive) {

        cacheNames.add(name);
        CacheConfigurationBuilder config = CacheConfigurationBuilder.newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(heapSize))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(timeToLive));
        Cache<K, V> myCache = cacheManager.createCache(name, config);
        return myCache;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        for (String cache : cacheNames) {
            this.cacheManager.removeCache(cache);
        }
        this.cacheManager.close();
    }
}
