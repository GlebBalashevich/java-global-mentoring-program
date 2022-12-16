package com.epam.mentoring.service;

import com.epam.mentoring.cache.lfu.LFUCache;
import com.epam.mentoring.model.Entry;

public class LFUCacheService {

    private LFUCache lfuCache;

    public LFUCacheService(LFUCache lfuCache) {
        this.lfuCache = lfuCache;
    }

    public Entry get(String data) {
        return lfuCache.get(data);
    }

    public Entry put(Entry entry) {
        return lfuCache.put(entry);
    }

}
