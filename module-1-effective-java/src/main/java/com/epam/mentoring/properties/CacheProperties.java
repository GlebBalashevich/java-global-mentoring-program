package com.epam.mentoring.properties;

public class CacheProperties {

    private int maxSize = 100000;

    private int expirationTime = 5;

    public CacheProperties() {
    }

    public CacheProperties(int maxSize, int expirationTime) {
        this.maxSize = maxSize;
        this.expirationTime = expirationTime;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

}
