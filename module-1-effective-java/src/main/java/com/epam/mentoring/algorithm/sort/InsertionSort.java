package com.epam.mentoring.algorithm.sort;

public class InsertionSort {

    public int[] sort(int[] array) {
        int[] sortedArray = new int[array.length];
        for (int i = 0; i < sortedArray.length; i++) {
            sortedArray[i] = array[i];
        }

        for (int i = 1; i < sortedArray.length; i++) {
            int element = sortedArray[i];
            int j = i - 1;
            while (j >= 0 && sortedArray[j] > element) {
                sortedArray[j + 1] = sortedArray[j];
                j = j - 1;
            }
            sortedArray[j + 1] = element;
        }

        return sortedArray;
    }

}
