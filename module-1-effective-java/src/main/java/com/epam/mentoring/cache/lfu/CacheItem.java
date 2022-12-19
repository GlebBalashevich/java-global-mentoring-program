package com.epam.mentoring.cache.lfu;

import java.time.Instant;
import java.util.Objects;

import com.epam.mentoring.model.Entry;
import com.epam.mentoring.util.Generated;

public class CacheItem {

    private Entry entry;

    private Instant expirationTime;

    private int numberOfUsage;

    private CacheItem prev;

    private CacheItem next;

    public CacheItem(Entry entry, Instant expirationTime, int numberOfUsage) {
        this.entry = entry;
        this.expirationTime = expirationTime;
        this.numberOfUsage = numberOfUsage;
    }

    public CacheItem() {
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
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

    public void setNumberOfUsage(int numberOfUsage) {
        this.numberOfUsage = numberOfUsage;
    }

    public void incrementNumberOfUsage() {
        this.numberOfUsage++;
    }

    public CacheItem getPrev() {
        return prev;
    }

    public void setPrev(CacheItem prev) {
        this.prev = prev;
    }

    public CacheItem getNext() {
        return next;
    }

    public void setNext(CacheItem next) {
        this.next = next;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CacheItem cacheItem = (CacheItem) o;

        if (numberOfUsage != cacheItem.numberOfUsage)
            return false;
        if (!entry.equals(cacheItem.entry))
            return false;
        if (!expirationTime.equals(cacheItem.expirationTime))
            return false;
        if (!Objects.equals(prev, cacheItem.prev))
            return false;
        return Objects.equals(next, cacheItem.next);
    }

    @Override
    @Generated
    public int hashCode() {
        int result = entry.hashCode();
        result = 31 * result + expirationTime.hashCode();
        result = 31 * result + numberOfUsage;
        result = 31 * result + (prev != null ? prev.hashCode() : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }

}
