package com.epam.mentoring.cache;

public class CacheMonitor {

    private long hitCount;

    private long puttedInCache;

    private long evictedFromCache;

    private long totalMillisToPut;

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public void incrementHitCount() {
        hitCount++;
    }

    public long getPuttedInCache() {
        return puttedInCache;
    }

    public void setPuttedInCache(long puttedInCache) {
        this.puttedInCache = puttedInCache;
    }

    public void incrementPuttedInCache() {
        puttedInCache++;
    }

    public long getEvictedFromCache() {
        return evictedFromCache;
    }

    public void setEvictedFromCache(long evictedFromCache) {
        this.evictedFromCache = evictedFromCache;
    }

    public void incrementEvictedFromCache() {
        evictedFromCache++;
    }

    public long getTotalMillisToPut() {
        return totalMillisToPut;
    }

    public void setTotalMillisToPut(long totalMillisToPut) {
        this.totalMillisToPut = totalMillisToPut;
    }

    public void addTotalMillisToPut(long totalMillisToPut) {
        this.totalMillisToPut += totalMillisToPut;
    }

    public double calculateAveragePutTime() {
        return (totalMillisToPut * 1.0) / puttedInCache;
    }

}
