package com.epam.mentoring.cache.lru;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.cache.CacheMonitor;
import com.epam.mentoring.cache.MangoCache;
import com.epam.mentoring.model.Entry;
import com.epam.mentoring.properties.CacheProperties;

public class LRUCache implements MangoCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LRUCache.class);

    private Cache<String, Entry> cache;

    private final CacheMonitor cacheMonitor;

    public LRUCache(CacheProperties cacheProperties) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cacheProperties.getMaxSize())
                .recordStats()
                .removalListener(new LRURemovalListener())
                .expireAfterAccess(cacheProperties.getExpirationTime(), TimeUnit.SECONDS)
                .build();
        this.cacheMonitor = new CacheMonitor();
    }

    @Override
    public Entry get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Entry put(Entry entry) {
        Instant startProcessingTime = Instant.now();
        cache.put(entry.getData(), entry);
        updateCacheMonitorOnPutItem(startProcessingTime);
        LOGGER.info("Entry with data:{} was saved in cache", entry.getData());
        return entry;
    }

    public CacheMonitor getCacheMonitor() {
        CacheStats cacheStats = cache.stats();
        cacheMonitor.setEvictedFromCache(cacheStats.evictionCount());
        cacheMonitor.setHitCount(cacheStats.hitCount());
        return this.cacheMonitor;
    }

    private void updateCacheMonitorOnPutItem(Instant startProcessingTime) {
        long processingTime = Instant.now().toEpochMilli() - startProcessingTime.toEpochMilli();
        cacheMonitor.addTotalMillisToPut(processingTime);
        cacheMonitor.incrementPuttedInCache();
    }

}
