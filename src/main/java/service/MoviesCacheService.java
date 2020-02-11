package service;

import org.ehcache.Cache;

public class MoviesCacheService<K, V> implements GenericCacheService<K, V> {

    private Cache<K, V> cache;

    public MoviesCacheService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public V find(K key) {
        return cache.get(key);
    }

    @Override
    public void save(K key, V value) {
        cache.put(key, value);
    }
}
