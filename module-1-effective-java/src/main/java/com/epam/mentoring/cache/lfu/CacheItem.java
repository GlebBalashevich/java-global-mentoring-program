package com.epam.mentoring.cache.lfu;

import java.time.Instant;

import com.epam.mentoring.model.Entry;

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

}
