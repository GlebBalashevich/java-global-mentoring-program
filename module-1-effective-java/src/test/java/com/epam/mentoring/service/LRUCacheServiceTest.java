package com.epam.mentoring.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.cache.lfu.LFUCache;
import com.epam.mentoring.cache.lru.LRUCache;
import com.epam.mentoring.model.Entry;
import com.epam.mentoring.properties.CacheProperties;

class LRUCacheServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LFUCache.class);

    @Test
    void testCacheEvictionByPutOrder() {
        CacheProperties cacheProperties = new CacheProperties(3, 5);
        LRUCache lruCache = new LRUCache(cacheProperties);
        CacheService lfuCacheService = new CacheService(lruCache);
        Entry entry1 = new Entry(UUID.randomUUID().toString());
        Entry entry2 = new Entry(UUID.randomUUID().toString());
        Entry entry3 = new Entry(UUID.randomUUID().toString());
        Entry entry4 = new Entry(UUID.randomUUID().toString());

        lfuCacheService.put(entry1);
        lfuCacheService.put(entry2);
        lfuCacheService.put(entry3);
        lfuCacheService.put(entry4);

        Assertions.assertThat(lfuCacheService.get(entry1.getData())).isNull();
        Assertions.assertThat(lfuCacheService.get(entry4.getData())).isNotNull();
        Assertions.assertThat(lruCache.getCacheMonitor().getPuttedInCache()).isEqualTo(4);
    }

    @Test
    void testCacheEvictionByOrderOfUse() {
        CacheProperties cacheProperties = new CacheProperties(3, 180);
        LRUCache lruCache = new LRUCache(cacheProperties);
        CacheService lfuCacheService = new CacheService(lruCache);
        Entry entry1 = new Entry(UUID.randomUUID().toString());
        Entry entry2 = new Entry(UUID.randomUUID().toString());
        Entry entry3 = new Entry(UUID.randomUUID().toString());
        Entry entry4 = new Entry(UUID.randomUUID().toString());
        Entry entry5 = new Entry(UUID.randomUUID().toString());

        lfuCacheService.put(entry1);
        lfuCacheService.put(entry2);
        lfuCacheService.get(entry2.getData());
        lfuCacheService.put(entry3);
        lfuCacheService.get(entry1.getData());
        lfuCacheService.put(entry3);
        lfuCacheService.put(entry4);
        lfuCacheService.put(entry5);

        Assertions.assertThat(lfuCacheService.get(entry1.getData())).isNull();
        Assertions.assertThat(lfuCacheService.get(entry2.getData())).isNull();
        Assertions.assertThat(lfuCacheService.get(entry3.getData())).isNotNull();
        Assertions.assertThat(lfuCacheService.get(entry4.getData())).isNotNull();
        Assertions.assertThat(lfuCacheService.get(entry5.getData())).isNotNull();
        Assertions.assertThat(lruCache.getCacheMonitor().getPuttedInCache()).isEqualTo(6);
        Assertions.assertThat(lruCache.getCacheMonitor().getHitCount()).isEqualTo(5);
        Assertions.assertThat(lruCache.getCacheMonitor().getEvictedFromCache()).isEqualTo(2);
    }

    @Test
    void testCacheEvictionByTime() throws InterruptedException {
        CacheProperties cacheProperties = new CacheProperties(5, 3);
        LRUCache lruCache = new LRUCache(cacheProperties);
        CacheService lfuCacheService = new CacheService(lruCache);
        Entry entry1 = new Entry(UUID.randomUUID().toString());
        Entry entry2 = new Entry(UUID.randomUUID().toString());
        Entry entry3 = new Entry(UUID.randomUUID().toString());
        Entry entry4 = new Entry(UUID.randomUUID().toString());

        lfuCacheService.put(entry1);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);
        lfuCacheService.put(entry2);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);
        lfuCacheService.put(entry3);
        lfuCacheService.put(entry4);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);

        Assertions.assertThat(lfuCacheService.get(entry1.getData())).isNull();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Assertions.assertThat(lfuCacheService.get(entry2.getData())).isNull();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Assertions.assertThat(lfuCacheService.get(entry3.getData())).isNull();
        Assertions.assertThat(lfuCacheService.get(entry4.getData())).isNull();
    }

    @Test
    void testCacheSize() {
        CacheProperties cacheProperties = new CacheProperties(100000, 100);
        LRUCache lruCache = new LRUCache(cacheProperties);
        CacheService lfuCacheService = new CacheService(lruCache);
        List<Entry> entries = Stream.generate(() -> new Entry(UUID.randomUUID().toString()))
                .limit(100000).toList();
        Entry first = entries.get(0);
        Entry last = entries.get(entries.size() - 1);

        entries.parallelStream().forEach(lfuCacheService::put);

        Assertions.assertThat(lfuCacheService.get(first.getData())).isNotNull();
        Assertions.assertThat(lfuCacheService.get(last.getData())).isNotNull();
        LOGGER.info("Average insertion time: {} millis", lruCache.getCacheMonitor().calculateAveragePutTime());
    }

}
