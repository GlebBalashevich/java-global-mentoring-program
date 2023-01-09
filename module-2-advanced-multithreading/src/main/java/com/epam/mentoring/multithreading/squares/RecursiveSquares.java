package com.epam.mentoring.multithreading.squares;

import java.util.concurrent.RecursiveAction;

public class RecursiveSquares extends RecursiveAction {

    private final double[] array;

    private final int lo;

    private final int hi;

    private double result;

    RecursiveSquares next; // keeps track of right-hand-side tasks

    RecursiveSquares(double[] array, int lo, int hi, RecursiveSquares next) {
        this.array = array;
        this.lo = lo;
        this.hi = hi;
        this.next = next;
    }

    private double atLeaf(int l, int h) {
        double sum = 0;
        for (int i = l; i < h; ++i) // perform leftmost base step
            sum += array[i] * array[i];
        return sum;
    }

    @Override
    protected void compute() {
        int l = lo;
        int h = hi;
        RecursiveSquares right = null;
        while (h - l > 1 && getSurplusQueuedTaskCount() <= 3) {
            int mid = (l + h) >>> 1;
            right = new RecursiveSquares(array, mid, h, right);
            right.fork();
            h = mid;
        }
        double sum = atLeaf(l, h);
        while (right != null) {
            if (right.tryUnfork()) // directly calculate if not stolen
                sum += right.atLeaf(right.lo, right.hi);
            else {
                right.join();
                sum += right.result;
            }
            right = right.next;
        }
        result = sum;
    }

    public double getResult() {
        return result;
    }

}
