package com.epam.mentoring.multithreading.blur;

import java.util.concurrent.RecursiveAction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ForkBlur extends RecursiveAction {

    private static final int S_THRESHOLD = 100000;

    private final int[] mSource;

    private final int mStart;

    private final int mLength;

    private final int[] mDestination;

    protected void compute() {
        if (mLength < S_THRESHOLD) {
            computeDirectly();
            return;
        }
        int split = mLength / 2;
        invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
                new ForkBlur(mSource, mStart + split, mLength - split, mDestination));
    }

    private void computeDirectly() {
        // Processing window size; should be odd.
        int mBlurWidth = 15;
        int sidePixels = (mBlurWidth - 1) / 2;
        for (int index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0),
                        mSource.length - 1);
                int pixel = mSource[mindex];
                rt += (float) ((pixel & 0x00ff0000) >> 16)
                        / mBlurWidth;
                gt += (float) ((pixel & 0x0000ff00) >> 8)
                        / mBlurWidth;
                bt += (float) ((pixel & 0x000000ff))
                        / mBlurWidth;
            }
            int dpixel = (0xff000000) |
                    (((int) rt) << 16) |
                    (((int) gt) << 8) |
                    (((int) bt));
            mDestination[index] = dpixel;
        }
    }

}
