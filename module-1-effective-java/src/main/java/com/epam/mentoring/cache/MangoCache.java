package com.epam.mentoring.cache;

import com.epam.mentoring.model.Entry;

public interface MangoCache {

    Entry get(String key);

    Entry put(Entry entry);

}
