package com.epam.mentoring.cache.lfu;

import java.time.Instant;

public class CacheItem {

    private String key;

    private Instant expirationTime;

    private int numberOfUsage;

    public CacheItem(String key, Instant expirationTime, int numberOfUsage) {
        this.key = key;
        this.expirationTime = expirationTime;
        this.numberOfUsage = numberOfUsage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getNumberOfUsage() {
        return numberOfUsage;
    }

    public void incrementNumberOfUsage() {
        this.numberOfUsage++;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CacheItem cacheItem = (CacheItem) o;

        if (numberOfUsage != cacheItem.numberOfUsage)
            return false;
        if (!key.equals(cacheItem.key))
            return false;
        return expirationTime.equals(cacheItem.expirationTime);
    }

    @Override public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + expirationTime.hashCode();
        result = 31 * result + numberOfUsage;
        return result;
    }

    @Override public String toString() {
        return "CacheItem{" +
                "key='" + key + '\'' +
                ", expirationTime=" + expirationTime +
                ", numberOfUsage=" + numberOfUsage +
                '}';
    }
}
