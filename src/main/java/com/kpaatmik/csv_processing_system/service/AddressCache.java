package com.kpaatmik.csv_processing_system.service;

import com.github.benmanes.caffeine.cache.LoadingCache;
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
                .expireAfterWrite(
                        1,
                        TimeUnit.HOURS
                )
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
}