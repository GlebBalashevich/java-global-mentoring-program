package com.epam.mentoring.multithreading.cli.command;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.epam.mentoring.multithreading.cli.model.DirectoryDescription;
import com.epam.mentoring.multithreading.cli.service.ProgressBarService;
import com.epam.mentoring.multithreading.cli.service.ScanService;

@Slf4j
@Command
@AllArgsConstructor
@NoArgsConstructor
public class ScanCommand implements Runnable {

    private static final String EXECUTION_MESSAGE = "Executed scanning directory: %s";

    private static final String PROCESSING_RESULT_MESSAGE = "%nTotal files: %d, total directories: %d, total memory size: %d, processing time: %ds%n";

    private ForkJoinPool forkJoinPool = new ForkJoinPool(10);

    @Option(names = { "-d", "-directory" }, required = true, description = "Scan directory")
    private String directory;

    @Override
    public void run() {
        System.out.printf(EXECUTION_MESSAGE, directory);
        ScanService scanService = new ScanService(new File(directory));
        executeProgressBar();
        Instant startExecution = Instant.now();
        DirectoryDescription directoryDescription = forkJoinPool.invoke(scanService);
        Instant endExecution = Instant.now();
        System.out.printf(PROCESSING_RESULT_MESSAGE, directoryDescription.getFileCount(),
                directoryDescription.getDirectoryCount(), directoryDescription.getMemorySize(),
                calculateProcessingInterval(startExecution, endExecution));
    }

    private long calculateProcessingInterval(Instant startTime, Instant endTime) {
        return TimeUnit.MILLISECONDS.toSeconds(
                endTime.toEpochMilli() - startTime.toEpochMilli());
    }

    private static void executeProgressBar() {
        Thread progressBar = new ProgressBarService();
        progressBar.setDaemon(true);
        progressBar.start();
    }

}
