package com.epam.mentoring.multithreading.fibonacci;

import java.util.concurrent.RecursiveTask;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RecursiveFibonacci extends RecursiveTask<Long> {

    private int number;

    @Override
    protected Long compute() {
        log.debug("number: {}", number);
        if (number <= 10) {
            return calculateSequentially(number);
        }
        RecursiveFibonacci recursiveTask1 = new RecursiveFibonacci(number - 1);
        RecursiveFibonacci recursiveTask2 = new RecursiveFibonacci(number - 2);
        invokeAll(recursiveTask1, recursiveTask2);
        return Long.sum(recursiveTask1.join(), recursiveTask2.join());
    }

    private Long calculateSequentially(int number) {
        log.debug("Sequential calculation for: {}", number);
        long n1 = 0L;
        long n2 = 1L;
        long result = 0L;
        while (number > 1) {
            result = n1 + n2;
            n1 = n2;
            n2 = result;
            number--;
        }
        return result;
    }

}
