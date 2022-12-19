package com.epam.mentoring.cache.lfu;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.cache.CacheMonitor;
import com.epam.mentoring.cache.MangoCache;
import com.epam.mentoring.model.Entry;
import com.epam.mentoring.properties.CacheProperties;

public class LFUCache extends TimerTask implements MangoCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LFUCache.class);

    private final ConcurrentMap<String, CacheItem> cache;

    private final CacheProperties cacheProperties;

    private final CacheMonitor cacheMonitor;

    private final LFURemovalListener lfuRemovalListener;

    private final CacheItem head;

    private final CacheItem tail;

    public LFUCache(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        this.cache = new ConcurrentHashMap<>();
        this.cacheMonitor = new CacheMonitor();
        this.lfuRemovalListener = new LFURemovalListener();
        this.head = new CacheItem();
        this.tail = new CacheItem();
        head.setNext(tail);
        tail.setPrev(head);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(this, 0, TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public Entry get(String key) {
        CacheItem cacheItem = cache.get(key);
        Entry entry = null;
        if (cacheItem != null) {
            updateItemUsage(cacheItem);
            cacheMonitor.incrementHitCount();
            move(cacheItem);
            entry = cacheItem.getEntry();
        }
        return entry;
    }

    @Override
    public Entry put(Entry entry) {
        CacheItem cacheItem;
        Instant startProcessingTime = Instant.now();
        if (!cache.containsKey(entry.getData())) {
            if (cache.size() == cacheProperties.getMaxSize()) {
                evictCache(tail.getPrev(), LFURemovalListener.Cause.SIZE);
            }
            cacheItem = new CacheItem(entry, calculateExpirationTime(), 0);
            cache.put(entry.getData(), cacheItem);
            LOGGER.info("Entry with data:{} was added to cache", entry.getData());
        } else {
            cacheItem = cache.get(entry.getData());
            updateItemUsage(cacheItem);
        }
        move(cacheItem);
        updateCacheMonitorOnPutItem(startProcessingTime);
        return cacheItem.getEntry();
    }

    @Override
    public void run() {
        List<CacheItem> itemsToRemove = cache.values().stream()
                .filter(cacheItem -> cacheItem.getExpirationTime().isBefore(Instant.now()))
                .toList();
        itemsToRemove.forEach(c -> evictCache(c, LFURemovalListener.Cause.EXPIRED));
    }

    public CacheMonitor getCacheMonitor() {
        return this.cacheMonitor;
    }

    private Instant calculateExpirationTime() {
        return Instant.now().plus(cacheProperties.getExpirationTime(), ChronoUnit.SECONDS);
    }

    private void updateItemUsage(CacheItem cacheItem) {
        cacheItem.incrementNumberOfUsage();
        cacheItem.setExpirationTime(Instant.now());
        unlink(cacheItem);
    }

    private void unlink(CacheItem cacheItem) {
        cacheItem.getPrev().setNext(cacheItem.getNext());
        cacheItem.getNext().setPrev(cacheItem.getPrev());
    }

    private void move(CacheItem cacheItem) {
        CacheItem current = head;
        while (current != null) {
            if (current.getNumberOfUsage() > cacheItem.getNumberOfUsage()) {
                current = current.getNext();
            } else {
                cacheItem.setPrev(current);
                cacheItem.setNext(current.getNext());
                current.getNext().setPrev(cacheItem);
                current.setNext(cacheItem);
                break;
            }
        }
    }

    private void evictCache(CacheItem removingItem, LFURemovalListener.Cause cause) {
        unlink(removingItem);
        cache.remove(removingItem.getEntry().getData());
        lfuRemovalListener.notify(removingItem.getEntry(), cause);
        cacheMonitor.incrementEvictedFromCache();
    }

    private void updateCacheMonitorOnPutItem(Instant startProcessingTime) {
        long processingTime = Instant.now().toEpochMilli() - startProcessingTime.toEpochMilli();
        cacheMonitor.addTotalMillisToPut(processingTime);
        cacheMonitor.incrementPuttedInCache();
    }

}
