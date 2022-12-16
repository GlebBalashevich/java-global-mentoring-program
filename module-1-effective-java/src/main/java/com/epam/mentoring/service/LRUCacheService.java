package com.epam.mentoring.service;

import com.epam.mentoring.cache.lru.LRUCache;

public class LRUCacheService {

    private LRUCache lruCache;

    public LRUCacheService(LRUCache lruCache) {
        this.lruCache = lruCache;
    }

}
