package com.epam.mentoring.multithreading.factorial;

import java.math.BigInteger;
import java.util.concurrent.RecursiveTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TreeFactorial extends RecursiveTask<BigInteger> {

    private final long right;

    private final long left;

    public TreeFactorial(long right) {
        this.right = right;
        this.left = 2;
    }

    private TreeFactorial(long left, long right) {
        this.left = left;
        this.right = right;
    }

    @Override
    protected BigInteger compute() {
        log.debug("FJP Task:{}, executed in thread:{}", this, Thread.currentThread().getName());
        BigInteger preCalculateResult = preCalculate(left, right);
        if (!preCalculateResult.equals(BigInteger.ZERO)) {
            return preCalculateResult;
        }
        long middle = calculateMidpoint(left, right);
        TreeFactorial leftBranch = new TreeFactorial(left, middle);
        TreeFactorial rightBranch = new TreeFactorial(middle + 1, right);
        leftBranch.fork();
        rightBranch.fork();
        return leftBranch.join().multiply(rightBranch.join());
    }

    public BigInteger sequenceApproach() {
        return sequenceApproach(left, right);
    }

    private BigInteger sequenceApproach(long left, long right) {
        log.debug("Sequence Task:{}, executed in thread:{}", this, Thread.currentThread().getName());
        BigInteger preCalculateResult = preCalculate(left, right);
        if (!preCalculateResult.equals(BigInteger.ZERO)) {
            return preCalculateResult;
        }
        long middle = calculateMidpoint(left, right);
        return sequenceApproach(left, middle).multiply(sequenceApproach(middle + 1, right));
    }

    private BigInteger preCalculate(long l, long r) {
        if (l > r) {
            return BigInteger.ONE;
        }
        if (l == r) {
            return BigInteger.valueOf(r);
        }
        if (r - left == 1) {
            return BigInteger.valueOf(r).multiply(BigInteger.valueOf(l));
        }
        return BigInteger.ZERO;
    }

    private long calculateMidpoint(long l, long r) {
        return (l + r) / 2;
    }

}
