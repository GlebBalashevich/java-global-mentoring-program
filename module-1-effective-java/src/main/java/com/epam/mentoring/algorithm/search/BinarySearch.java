package com.epam.mentoring.algorithm.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinarySearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySearch.class);

    public int iterativeBinarySearch(int[] sortedArray, int key) {
        int index = -1;
        int low = 0;
        int high = sortedArray.length - 1;
        while (low < high) {
            int mid = low + ((high - low) / 2);
            if (sortedArray[mid] < key) {
                low = mid + 1;
                LOGGER.debug("mid value LESS than key, low:{}, mid:{}, high:{}", low, mid, high);
            } else if (sortedArray[mid] > key) {
                high = mid - 1;
                LOGGER.debug("mid value MORE than key, low:{}, mid:{}, high:{}", low, mid, high);
            } else {
                index = mid;
                LOGGER.debug("index found:{}", index);
                break;
            }
        }
        return index;
    }

    public int recursiveBinarySearch(int[] sortedArray, int key, int low, int high) {
        if (high < low) {
            return -1;
        }
        int mid = low + ((high - low) / 2);
        if (sortedArray[mid] < key) {
            LOGGER.debug("mid value LESS than key, low:{}, mid:{}, high:{}", low, mid, high);
            return recursiveBinarySearch(sortedArray, key, mid + 1, high);
        } else if (sortedArray[mid] > key) {
            LOGGER.debug("mid value MORE than key, low:{}, mid:{}, high:{}", low, mid, high);
            return recursiveBinarySearch(sortedArray, key, low, mid - 1);
        } else {
            LOGGER.debug("index found:{}", mid);
            return mid;
        }
    }

}
