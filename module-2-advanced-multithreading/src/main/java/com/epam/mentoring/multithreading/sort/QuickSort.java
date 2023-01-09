package com.epam.mentoring.multithreading.sort;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickSort extends RecursiveTask<int[]> {

    private final int[] array;

    private final int begin;

    private final int end;

    public QuickSort(int[] array) {
        this.array = Arrays.copyOf(array, array.length);
        this.begin = 0;
        this.end = array.length;
    }

    private QuickSort(int[] array, int begin, int end) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected int[] compute() {
        return recursiveSort(array, begin, end);
    }

    private int[] recursiveSort(int[] array, int begin, int end) {
        log.debug("begin: {}, end:{}, thread:{}", begin, end, Thread.currentThread().getName());
        if (array.length == 0) {
            return array;
        }
        if (begin >= end) {
            return array;
        }
        int k = end;
        int pivot = array[begin];

        for (int i = end - 1; i > begin; i--) {
            if (array[i] >= pivot) {
                k--;
                if (i != k) {
                    swapElements(k, i);
                }
            }
        }

        int pivotIndex = --k;
        swapElements(k, begin);

        QuickSort left = new QuickSort(array, begin, pivotIndex);
        QuickSort right = new QuickSort(array, pivotIndex + 1, end);
        left.fork();
        right.fork();
        left.join();
        right.join();

        return array;
    }

    private void swapElements(int left, int right) {
        int swap = array[left];
        array[left] = array[right];
        array[right] = swap;
    }

}
