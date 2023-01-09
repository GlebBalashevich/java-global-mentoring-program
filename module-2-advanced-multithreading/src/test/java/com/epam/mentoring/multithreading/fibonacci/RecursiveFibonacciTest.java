package com.epam.mentoring.multithreading.fibonacci;

import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RecursiveFibonacciTest {

    @Test
    void testRecursiveFibonacci() {
        RecursiveFibonacci recursiveFibonacci = new RecursiveFibonacci(45);
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);

        Long actual = forkJoinPool.invoke(recursiveFibonacci);

        Assertions.assertThat(actual).isEqualTo(1134903170L);
    }

}
