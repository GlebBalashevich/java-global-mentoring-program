package com.epam.mentoring.multithreading.cli.service;

import java.io.File;
import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.mentoring.multithreading.cli.model.DirectoryDescription;

class ScanServiceTest {

    @Test
    void testScanService() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        ScanService scanService = new ScanService(new File("src/test/resources/directorytest1"));

        DirectoryDescription actual = forkJoinPool.invoke(scanService);

        Assertions.assertThat(actual.getFileCount()).isEqualTo(5);
        Assertions.assertThat(actual.getDirectoryCount()).isEqualTo(4);
        Assertions.assertThat(actual.getMemorySize()).isEqualTo(15000);
    }

}
