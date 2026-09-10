package com.kpaatmik.csv_processing_system.service;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AddressCache {

    private final LoadingCache<String, Long> cache;

    public AddressCache(
            AddressLoader addressLoader) {

        this.cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
                .build(addressLoader::load);
    }

    public Long get(String zipCode) {
        return cache.get(zipCode);
    }

    public void evict(String zipCode) {
        cache.invalidate(zipCode);
    }

    public void clear() {
        cache.invalidateAll();
    }
    
    public void printStats() {

        CacheStats stats = cache.stats();

        System.out.println("========== CACHE STATISTICS ==========");

        System.out.println(
                "Cache hits    : " + stats.hitCount()
        );

        System.out.println(
                "Cache misses  : " + stats.missCount()
        );

        System.out.println(
                "Hit rate      : " + stats.hitRate()
        );

        System.out.println(
                "Load count    : " + stats.loadCount()
        );

        System.out.println(
                "Load time     : " + stats.totalLoadTime() + " ns"
        );

        System.out.println("======================================");
    }
    
}