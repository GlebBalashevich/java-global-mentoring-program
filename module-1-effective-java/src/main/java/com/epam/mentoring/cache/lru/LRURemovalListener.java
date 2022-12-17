package com.epam.mentoring.cache.lru;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.model.Entry;

public class LRURemovalListener implements RemovalListener<String, Entry> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LRURemovalListener.class);

    @Override
    public void onRemoval(RemovalNotification<String, Entry> notification) {
        LOGGER.info("Entry with data:{} was removed due {}", notification.getKey(), notification.getCause());
    }

}
