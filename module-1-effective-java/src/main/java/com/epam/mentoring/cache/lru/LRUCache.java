package com.epam.mentoring.cache.lru;

import com.epam.mentoring.properties.CacheProperties;

public class LRUCache {

    private CacheProperties cacheProperties;

    public LRUCache(CacheProperties cacheProperties){
        this.cacheProperties = cacheProperties;
    }

}
