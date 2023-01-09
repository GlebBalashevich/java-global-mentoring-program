package com.epam.mentoring.multithreading.cli.command;

import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.epam.mentoring.multithreading.cli.model.DirectoryDescription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScanCommandTest {

    @Mock
    private ForkJoinPool forkJoinPool;

    @Test
    void testScanDirectory() {
        forkJoinPool = mock(ForkJoinPool.class);
        ScanCommand scanCommand = new ScanCommand(forkJoinPool, "src/test/resources/directorytest1");
        Thread thread = new Thread(scanCommand);

        when(forkJoinPool.invoke(any())).thenReturn(DirectoryDescription.builder().build());

        thread.run(); // we don't need to start thread, only execute run method

        verify(forkJoinPool).invoke(any());
    }

}
