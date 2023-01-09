package com.epam.mentoring.multithreading.sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.mentoring.algorithm.sort.MergeSort;

class QuickSortTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuickSortTest.class);

    @Test
    void arraySortTest() {
        Random random = new Random();
        int[] array = random.ints().limit(1000000).toArray();
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        QuickSort quickSort = new QuickSort(array);
        int[] expected = Arrays.stream(array).sorted().toArray();

        int[] actual = forkJoinPool.invoke(quickSort);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideDifferentLengthArray")
    void arraySortPerformanceTest(int[] array) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        QuickSort quickSort = new QuickSort(array);
        MergeSort mergeSort = new MergeSort();

        Instant startFJPSortProcessing = Instant.now();
        int[] sortedFJPArray = forkJoinPool.invoke(quickSort);
        Instant endFJPSortProcessing = Instant.now();

        Instant startSeqSortProcessing = Instant.now();
        int[] sortedSeqArray = mergeSort.sort(array);
        Instant endSeqSortProcessing = Instant.now();

        Assertions.assertThat(sortedFJPArray).isEqualTo(sortedSeqArray);

        LOGGER.info("Array length:{} FJP Quick Sort - {}ms | Seq Merge Sort - {}ms", array.length,
                endFJPSortProcessing.toEpochMilli() - startFJPSortProcessing.toEpochMilli(),
                endSeqSortProcessing.toEpochMilli() - startSeqSortProcessing.toEpochMilli());

        // [2022-12-22T17:06:07.951] [Test worker] [INFO] QuickSortTest - Array length:100 FJP Quick Sort - 4ms | Seq
        // Merge Sort - 1ms
        // [2022-12-22T17:06:07.988] [Test worker] [INFO] QuickSortTest - Array length:1000 FJP Quick Sort - 6ms | Seq
        // Merge Sort - 1ms
        // [2022-12-22T17:06:08.017] [Test worker] [INFO] QuickSortTest - Array length:10000 FJP Quick Sort - 17ms | Seq
        // Merge Sort - 5ms
        // [2022-12-22T17:06:08.153] [Test worker] [INFO] QuickSortTest - Array length:100000 FJP Quick Sort - 75ms |
        // Seq Merge Sort - 35ms
        // [2022-12-22T17:06:08.749] [Test worker] [INFO] QuickSortTest - Array length:1000000 FJP Quick Sort - 292ms |
        // Seq Merge Sort - 196ms
        // [2022-12-22T17:06:13.520] [Test worker] [INFO] QuickSortTest - Array length:10000000 FJP Quick Sort - 1922ms
        // | Seq Merge Sort - 2478ms
    }

    private static Stream<int[]> provideDifferentLengthArray() {
        Random random = new Random();
        int[] array100 = random.ints().limit(100).toArray();
        int[] array1000 = random.ints().limit(1000).toArray();
        int[] array10000 = random.ints().limit(10000).toArray();
        int[] array100000 = random.ints().limit(100000).toArray();
        int[] array1000000 = random.ints().limit(1000000).toArray();
        int[] array10000000 = random.ints().limit(10000000).toArray();
        return Stream.of(array100, array1000, array10000, array100000, array1000000, array10000000);
    }

}
