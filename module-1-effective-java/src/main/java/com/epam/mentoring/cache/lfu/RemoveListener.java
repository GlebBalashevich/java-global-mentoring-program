package com.epam.mentoring.cache.lfu;

import java.time.Instant;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.epam.mentoring.cache.CacheMonitor;
import com.epam.mentoring.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveListener extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveListener.class);

    private final ConcurrentMap<String, Entry> map;

    private final SortedSet<CacheItem> cacheItems;

    private final CacheMonitor cacheMonitor;

    public RemoveListener(ConcurrentMap<String, Entry> map, SortedSet<CacheItem> cacheItems, CacheMonitor cacheMonitor) {
        this.map = map;
        this.cacheItems = cacheItems;
        this.cacheMonitor = cacheMonitor;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 0, TimeUnit.SECONDS.toMillis(1));
    }

    @Override public void run() {
        map.keySet().stream()
                .filter(key -> cacheItems.removeIf(
                        c -> c.getKey().equals(key) && c.getExpirationTime().isBefore(Instant.now())))
                .map(map::remove)
                .forEach(entry -> {
                    cacheMonitor.incrementEvictedFromCache();
                    LOGGER.info("Entry with data:{} was removed due eviction time", entry.getData());
                });
    }
}
