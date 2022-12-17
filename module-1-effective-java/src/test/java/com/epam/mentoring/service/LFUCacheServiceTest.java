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
import com.epam.mentoring.model.Entry;
import com.epam.mentoring.properties.CacheProperties;

class LFUCacheServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LFUCache.class);

    @Test
    void testCacheEvictionByPutOrder() {
        CacheProperties cacheProperties = new CacheProperties(3, 5);
        LFUCache lfuCache = new LFUCache(cacheProperties);
        CacheService lfuCacheService = new CacheService(lfuCache);
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
        Assertions.assertThat(lfuCache.getCacheMonitor().getPuttedInCache()).isEqualTo(4);
    }

    @Test
    void testCacheEvictionByFrequencyOfUse() {
        CacheProperties cacheProperties = new CacheProperties(3, 180);
        LFUCache lfuCache = new LFUCache(cacheProperties);
        CacheService cacheService = new CacheService(lfuCache);
        Entry entry1 = new Entry("1");
        Entry entry2 = new Entry("2");
        Entry entry3 = new Entry("3");
        Entry entry4 = new Entry("4");
        Entry entry5 = new Entry("5");

        cacheService.put(entry1);
        cacheService.put(entry2);
        cacheService.get(entry2.getData());
        cacheService.put(entry3);
        cacheService.get(entry1.getData());
        cacheService.put(entry3);
        cacheService.put(entry4);
        cacheService.put(entry5);

        Assertions.assertThat(cacheService.get(entry1.getData())).isNull();
        Assertions.assertThat(cacheService.get(entry2.getData())).isNull();
        Assertions.assertThat(cacheService.get(entry3.getData())).isNotNull();
        Assertions.assertThat(cacheService.get(entry4.getData())).isNotNull();
        Assertions.assertThat(cacheService.get(entry5.getData())).isNotNull();
        Assertions.assertThat(lfuCache.getCacheMonitor().getPuttedInCache()).isEqualTo(6);
        Assertions.assertThat(lfuCache.getCacheMonitor().getHitCount()).isEqualTo(5);
        Assertions.assertThat(lfuCache.getCacheMonitor().getEvictedFromCache()).isEqualTo(2);
    }

    @Test
    void testCacheEvictionByTime() throws InterruptedException {
        CacheProperties cacheProperties = new CacheProperties(5, 3);
        LFUCache lfuCache = new LFUCache(cacheProperties);
        CacheService cacheService = new CacheService(lfuCache);
        Entry entry1 = new Entry(UUID.randomUUID().toString());
        Entry entry2 = new Entry(UUID.randomUUID().toString());
        Entry entry3 = new Entry(UUID.randomUUID().toString());
        Entry entry4 = new Entry(UUID.randomUUID().toString());

        cacheService.put(entry1);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);
        cacheService.put(entry2);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);
        cacheService.put(entry3);
        cacheService.put(entry4);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1000);

        Assertions.assertThat(cacheService.get(entry1.getData())).isNull();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Assertions.assertThat(cacheService.get(entry2.getData())).isNull();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Assertions.assertThat(cacheService.get(entry3.getData())).isNull();
        Assertions.assertThat(cacheService.get(entry4.getData())).isNull();
    }

    @Test
    void testCacheSize() {
        CacheProperties cacheProperties = new CacheProperties(100000, 5);
        LFUCache lfuCache = new LFUCache(cacheProperties);
        CacheService cacheService = new CacheService(lfuCache);
        List<Entry> entries = Stream.generate(() -> new Entry(UUID.randomUUID().toString()))
                .limit(100000).toList();
        Entry first = entries.get(0);
        Entry last = entries.get(entries.size() - 1);

        entries.stream().forEach(lfuCache::put);

        Assertions.assertThat(cacheService.get(first.getData())).isNotNull();
        Assertions.assertThat(cacheService.get(last.getData())).isNotNull();
        LOGGER.info("Average insertion time: {} millis", lfuCache.getCacheMonitor().calculateAveragePutTime());
        LOGGER.info("Average insertion time: {} millis", lfuCache.getCacheMonitor().getPuttedInCache());
    }

}
