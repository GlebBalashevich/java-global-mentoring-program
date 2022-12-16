package com.epam.mentoring.cache.lfu;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.epam.mentoring.cache.CacheMonitor;
import com.epam.mentoring.model.Entry;
import com.epam.mentoring.properties.CacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LFUCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LFUCache.class);

    private final ConcurrentMap<String, Entry> cache;

    private final SortedSet<CacheItem> cacheItems;

    private final CacheProperties cacheProperties;

    private final CacheMonitor cacheMonitor;

    private final RemoveListener removeListener;

    public LFUCache(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        this.cache = new ConcurrentHashMap<>();
        this.cacheItems = Collections.synchronizedSortedSet(new TreeSet<>(Comparator
                .comparingInt(CacheItem::getNumberOfUsage)
                .thenComparing(CacheItem::getExpirationTime)
                .thenComparing(CacheItem::getKey)));
        this.cacheMonitor = new CacheMonitor();
        this.removeListener = new RemoveListener(cache, cacheItems, cacheMonitor);
    }

    public Entry get(String key) {
        Entry entry = cache.get(key);
        if (entry != null) {
            updateCacheItem(entry);
            cacheMonitor.incrementHitCount();
        }
        return entry;
    }

    public Entry put(Entry entry) {
        if (!cache.containsKey(entry.getData())) {
            Instant startProcessingTime = Instant.now();
            freeUpCacheSpace();
            cache.put(entry.getData(), entry);
            addCacheItem(entry);
            updateCacheMonitorOnPutItem(startProcessingTime);
            LOGGER.info("Entry with data:{} was saved in cache", entry.getData());
        } else {
            updateCacheItem(entry);
        }
        return entry;
    }

    private void freeUpCacheSpace() {
        while (cache.size() >= cacheProperties.getMaxSize()) {
            CacheItem cacheItem = cacheItems.first();
            if (cacheItem != null) {
                cacheItems.remove(cacheItem);
                Entry entry = cache.remove(cacheItem.getKey());
                cacheMonitor.incrementEvictedFromCache();
                LOGGER.info("Entry with data:{} was removed by eviction capacity", entry.getData());
            }
        }
    }

    private void addCacheItem(Entry entry) {
        cacheItems.add(new CacheItem(entry.getData(), calculateExpirationTime(), 0));
    }

    private void updateCacheItem(Entry entry) {
        Optional<CacheItem> cacheItemOptional = cacheItems.stream()
                .filter(c -> c.getKey().equals(entry.getData()))
                .findFirst();
        if (cacheItemOptional.isPresent()) {
            CacheItem cacheItem = cacheItemOptional.get();
            cacheItems.remove(cacheItem);
            cacheItem.setExpirationTime(calculateExpirationTime());
            cacheItem.incrementNumberOfUsage();
            cacheItems.add(cacheItem);
        }
    }

    private Instant calculateExpirationTime() {
        return Instant.now().plus(cacheProperties.getExpirationTime(), ChronoUnit.SECONDS);
    }

    private void updateCacheMonitorOnPutItem(Instant startProcessingTime) {
        long processingTime = startProcessingTime.toEpochMilli() - Instant.now().toEpochMilli();
        cacheMonitor.addTotalMillisToPut(processingTime);
        cacheMonitor.incrementPuttedInCache();
    }

}
