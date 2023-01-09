package com.epam.mentoring.multithreading.cli.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DirectoryDescription {

    private long fileCount;

    private long directoryCount;

    private long memorySize;

    public void addFileCount(long fileCount) {
        this.fileCount += fileCount;
    }

    public void addDirectoryCount(long directoryCount) {
        this.directoryCount += directoryCount;
    }

    public void addMemorySize(long memorySize) {
        this.memorySize += memorySize;
    }

}
