package com.epam.mentoring.service;

import com.epam.mentoring.cache.MangoCache;
import com.epam.mentoring.model.Entry;

public class CacheService {

    private MangoCache mangoCache;

    public CacheService(MangoCache mangoCache) {
        this.mangoCache = mangoCache;
    }

    public Entry get(String data) {
        return mangoCache.get(data);
    }

    public Entry put(Entry entry) {
        return mangoCache.put(entry);
    }

}
