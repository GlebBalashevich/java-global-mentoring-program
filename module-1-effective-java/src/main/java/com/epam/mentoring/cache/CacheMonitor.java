package com.epam.mentoring.cache;

public class CacheMonitor {

    private long hitCount;

    private long puttedInCache;

    private long evictedFromCache;

    private long totalMillisToPut;

    public long getHitCount() {
        return hitCount;
    }

    public void incrementHitCount() {
        this.hitCount++;
    }

    public long getPuttedInCache() {
        return puttedInCache;
    }

    public void incrementPuttedInCache() {
        this.puttedInCache++;
    }

    public long getEvictedFromCache() {
        return evictedFromCache;
    }

    public void incrementEvictedFromCache() {
        this.evictedFromCache++;
    }

    public long getTotalMillisToPut() {
        return totalMillisToPut;
    }

    public void addTotalMillisToPut(long totalMillisToPut) {
        this.totalMillisToPut += totalMillisToPut;
    }
}
