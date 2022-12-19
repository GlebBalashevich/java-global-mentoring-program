package com.epam.mentoring.algorithm.sort;

public class MergeSort {

    public int[] sort(int[] array) {
        if (array.length == 1) {
            return array;
        }

        int mid = array.length / 2;
        int[] left = new int[mid];
        int[] right = new int[array.length - mid];

        for (int i = 0; i < mid; i++) {
            left[i] = array[i];
        }
        for (int i = mid; i < array.length; i++) {
            right[i - mid] = array[i];
        }

        left = sort(left);
        right = sort(right);

        return merge(left, right);
    }

    private int[] merge(int[] left, int[] right) {
        int[] sortedArray = new int[left.length + right.length];
        int si = 0;
        int li = 0;
        int ri = 0;

        while (li < left.length && ri < right.length) {
            if (left[li] <= right[ri]) {
                sortedArray[si++] = left[li++];
            } else {
                sortedArray[si++] = right[ri++];
            }
        }

        while (li < left.length) {
            sortedArray[si++] = left[li++];
        }
        while (ri < right.length) {
            sortedArray[si++] = right[ri++];
        }

        return sortedArray;
    }

}
