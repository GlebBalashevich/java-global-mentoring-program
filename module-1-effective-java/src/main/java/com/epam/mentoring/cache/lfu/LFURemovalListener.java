package com.epam.mentoring.cache.lfu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.model.Entry;

public class LFURemovalListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LFURemovalListener.class);

    public void notify(Entry entry, Cause cause) {
        onRemoval(entry, cause);
    }

    public void onRemoval(Entry entry, Cause cause) {
        LOGGER.info("Entry with data:{} was removed due {}", entry.getData(), cause);
    }

    enum Cause {
        EXPIRED, SIZE
    }

}
