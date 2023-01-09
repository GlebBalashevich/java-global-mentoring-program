package com.epam.mentoring.multithreading.cli.service;

import java.io.File;
import java.util.concurrent.RecursiveTask;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.epam.mentoring.multithreading.cli.model.DirectoryDescription;

@Slf4j
@AllArgsConstructor
public class ScanService extends RecursiveTask<DirectoryDescription> {

    private final File rootFile;

    @Override
    protected DirectoryDescription compute() {
        log.debug("scanning directory:{}", rootFile.getPath());
        DirectoryDescription directoryDescription = DirectoryDescription.builder().build();
        if (rootFile.isFile()) {
            directoryDescription.addFileCount(1L);
            directoryDescription.addMemorySize(rootFile.length());
        }
        if (rootFile.isDirectory()) {
            directoryDescription.addDirectoryCount(1L);
            File[] directoryFiles = rootFile.listFiles();
            if (directoryFiles != null) {
                for (File file : directoryFiles) {
                    ScanService childScanService = new ScanService(file);
                    childScanService.fork();
                    DirectoryDescription childDirectoryDescription = childScanService.join();
                    mergeDirectoryDescriptions(directoryDescription, childDirectoryDescription);
                }
            }
        }
        return directoryDescription;
    }

    private void mergeDirectoryDescriptions(DirectoryDescription directoryDescription,
            DirectoryDescription childDirectoryDescription) {
        directoryDescription.addFileCount(childDirectoryDescription.getFileCount());
        directoryDescription.addDirectoryCount(childDirectoryDescription.getDirectoryCount());
        directoryDescription.addMemorySize(childDirectoryDescription.getMemorySize());
    }

}
