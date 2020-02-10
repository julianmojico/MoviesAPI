package service;

import org.ehcache.Cache;

public class MoviesCacheService implements GenericCacheService{

    private Cache cache;

    public MoviesCacheService(Cache cache){
        this.cache = cache;
    }
    @Override
    public Object find(Object key) {
        return cache.get(key);
    }

    @Override
    public void save(Object key, Object value) {
        cache.put(key,value);
    }

    @Override
    public void close(String name) {

    }
}
