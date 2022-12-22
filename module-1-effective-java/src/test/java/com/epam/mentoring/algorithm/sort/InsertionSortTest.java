package com.epam.mentoring.algorithm.sort;

import java.util.Random;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertionSortTest {

    private InsertionSort insertionSort;

    @BeforeEach
    void init() {
        insertionSort = new InsertionSort();
    }

    @Test
    void testInsertionSortArray() {
        Random random = new Random();
        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        int[] actual = insertionSort.sort(array);

        Assertions.assertThat(actual[0] < actual[10000]).isTrue();
        Assertions.assertThat(actual[10000] < actual[20000]).isTrue();
        Assertions.assertThat(actual[20000] < actual[30000]).isTrue();
        Assertions.assertThat(actual[30000] < actual[40000]).isTrue();
        Assertions.assertThat(actual[40000] < actual[50000]).isTrue();
        Assertions.assertThat(actual[50000] < actual[60000]).isTrue();
        Assertions.assertThat(actual[60000] < actual[70000]).isTrue();
        Assertions.assertThat(actual[70000] < actual[80000]).isTrue();
        Assertions.assertThat(actual[80000] < actual[90000]).isTrue();
        Assertions.assertThat(actual[90000] < actual[99999]).isTrue();
    }

}
