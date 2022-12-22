package com.epam.mentoring.algorithm.search;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinarySearchTest {

    private BinarySearch binarySearch;

    @BeforeEach
    void init() {
        binarySearch = new BinarySearch();
    }

    @Test
    void testIterativeBinarySearchKeyFound() {
        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        int actual = binarySearch.iterativeBinarySearch(array, 51000);

        Assertions.assertThat(actual).isEqualTo(51000);
        Assertions.assertThat(array[actual]).isEqualTo(51000);
    }

    @Test
    void testIterativeBinarySearchKeyNOTFound() {
        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        int actual = binarySearch.iterativeBinarySearch(array, 100500);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    void testRecursiveBinarySearchKeyFound() {
        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        int actual = binarySearch.recursiveBinarySearch(array, 51000, 0, array.length - 1);

        Assertions.assertThat(actual).isEqualTo(51000);
        Assertions.assertThat(array[actual]).isEqualTo(51000);
    }

    @Test
    void testRecursiveBinarySearchKeyNOTFound() {
        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        int actual = binarySearch.recursiveBinarySearch(array, 100500, 0, array.length - 1);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

}
