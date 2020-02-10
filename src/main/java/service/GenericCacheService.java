package service;

import java.util.Optional;

public interface GenericCacheService<K,V> {

    Object find(K key);
    void save(K key, V value);
    void close(String name);
}
