package com.epam.mentoring.benchmarks.alrotihm.search;

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

import com.epam.mentoring.algorithm.search.BinarySearch;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@BenchmarkMode({ Mode.Throughput, Mode.AverageTime, Mode.SampleTime })
@Measurement(iterations = 5)
public class BenchmarkSearch {

    @Benchmark
    public void iterativeSearch(BenchmarkState benchmarkState) {
        benchmarkState.binarySearch.iterativeBinarySearch(benchmarkState.sortedArray, benchmarkState.key);
    }

    @Benchmark
    public void recursiveSearch(BenchmarkState benchmarkState) {
        benchmarkState.binarySearch.recursiveBinarySearch(benchmarkState.sortedArray, benchmarkState.key, 0,
                benchmarkState.sortedArray.length - 1);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({ "10", "100", "1000", "10000", "100000" })
        public int size;

        public int key;

        public int[] sortedArray;

        public BinarySearch binarySearch;

        @Setup
        public void setup() {
            Random random = new Random();
            binarySearch = new BinarySearch();
            sortedArray = random.ints(size).sorted().toArray();
            key = sortedArray[size - 2];
        }

    }

}
