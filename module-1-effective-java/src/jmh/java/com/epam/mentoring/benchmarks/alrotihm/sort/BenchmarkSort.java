package com.epam.mentoring.benchmarks.alrotihm.sort;

import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.epam.mentoring.algorithm.sort.InsertionSort;
import com.epam.mentoring.algorithm.sort.MergeSort;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
@Measurement(iterations = 5)
public class BenchmarkSort {

    @Benchmark
    public void mergeSort(BenchmarkState benchmarkState) {
        benchmarkState.mergeSort.sort(benchmarkState.array);
    }

    @Benchmark
    public void insertionSort(BenchmarkState benchmarkState) {
        benchmarkState.insertionSort.sort(benchmarkState.array);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({ "10", "100", "1000", "10000", "100000" })
        public int size;

        public int[] array;

        public InsertionSort insertionSort;

        public MergeSort mergeSort;

        @Setup
        public void setup() {
            Random random = new Random();
            insertionSort = new InsertionSort();
            mergeSort = new MergeSort();
            array = new int[size];
            array = random.ints(size).toArray();
        }

    }

}
