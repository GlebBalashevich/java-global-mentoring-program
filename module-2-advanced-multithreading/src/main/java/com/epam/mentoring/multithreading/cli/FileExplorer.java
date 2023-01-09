package com.epam.mentoring.multithreading.cli;

import picocli.CommandLine;
import sun.misc.Signal;

import com.epam.mentoring.multithreading.cli.command.ScanCommand;

public class FileExplorer {

    private static final String INTERRUPTED_PROCESSING_MESSAGE = "%nProcessing was interrupted by user";

    public static void main(String[] args) {
        registerSignalHandler();
        int exitCode = new CommandLine(new ScanCommand()).execute(args);
        System.exit(exitCode);
    }

    private static void registerSignalHandler() {
        Signal.handle(new Signal("INT"), sig -> {
            System.out.printf(INTERRUPTED_PROCESSING_MESSAGE);
            System.exit(130);
        });
    }

}
