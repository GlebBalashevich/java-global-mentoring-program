package com.epam.mentoring.multithreading.cli.service;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressBarService extends Thread {

    private static final String INITIAL_PROGRESS_CAPTION = "%nProgress:|";

    private static final String PROGRESS_CHAR = "=";

    @Override
    public void run() {
        try {
            System.out.printf(INITIAL_PROGRESS_CAPTION);
            while (true) {
                System.out.print(PROGRESS_CHAR);
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            }
        } catch (InterruptedException e) {
            log.error("Progress Bar thread was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

}
