package com.epam.mentoring.multithreading.squares;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RecursiveSquaresTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecursiveSquaresTest.class);

    @Test
    void testRecursiveDoubleSquares() {
        Random random = new Random();
        double[] array = random.doubles(0, 10).limit(500000000).toArray();
        RecursiveSquares recursiveSquares = new RecursiveSquares(array, 0, array.length, null);
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);

        Instant startFJPSquareProcessing = Instant.now();
        forkJoinPool.invoke(recursiveSquares);
        double recursiveResult = recursiveSquares.getResult();
        Instant endFJPSquareProcessing = Instant.now();

        Instant startSeqSquaresProcessing = Instant.now();
        double streamResult = Arrays.stream(array).reduce(0, (sum, d) -> sum + Math.pow(d, 2));
        Instant endSeqSquareProcessing = Instant.now();

        LOGGER.info("Array length:{} FJP Square - {}ms | Seq Square - {}ms", array.length,
                endFJPSquareProcessing.toEpochMilli() - startFJPSquareProcessing.toEpochMilli(),
                endSeqSquareProcessing.toEpochMilli() - startSeqSquaresProcessing.toEpochMilli());

        // RecursiveSquaresTest - Array length:500000000 FJP Square - 176ms | Seq Square - 694ms

        Assertions.assertThat(recursiveResult).isCloseTo(streamResult, Offset.offset(0.1));
    }

}
