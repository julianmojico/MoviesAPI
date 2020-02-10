package service;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.Ehcache;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class EhCacheInitializer {

    private CacheManager cacheManager;

    public EhCacheInitializer() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .build();
        cacheManager.init();
    }

    public <K, V> Cache createCache(String name, Class<K> keyClass, Class<V> valueClass, int heapSize) {

        CacheConfigurationBuilder config = CacheConfigurationBuilder.newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(heapSize));
        Cache<K, V> myCache = cacheManager.createCache(name, config);
        return myCache;
    }

    public void close(String name) {
        this.cacheManager.removeCache(name);
    }

    public void closeManager() {
        this.cacheManager.close();
    }
}
