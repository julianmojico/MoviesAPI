package service;

public interface GenericCacheService<K,V> {

    V find(K key);
    void save(K key, V value);
}
