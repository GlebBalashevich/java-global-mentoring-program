package com.epam.mentoring.multithreading.blur;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;

import javax.imageio.ImageIO;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ForkBlurTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForkBlurTest.class);

    @Test
    void testForBlur() throws IOException {
        String srcName = "src/test/resources/image/original/java-logo.png";
        String distName = "src/test/resources/image/blurred/java-logo.png";
        File srcFile = new File(srcName);
        File distFile = new File(distName);
        BufferedImage image = ImageIO.read(srcFile);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] src = image.getRGB(0, 0, width, height, null, 0, width);
        int[] dist = new int[src.length];
        ForkBlur forkBlur = new ForkBlur(src, 0, src.length, dist);
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);

        Instant startBlurProcessing = Instant.now();
        forkJoinPool.invoke(forkBlur);
        Instant endBlurProcessing = Instant.now();

        BufferedImage distImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        distImage.setRGB(0, 0, width, height, dist, 0, width);
        ImageIO.write(distImage, "png", distFile);

        LOGGER.info("Image blurring processed in :{}ms",
                endBlurProcessing.toEpochMilli() - startBlurProcessing.toEpochMilli());

        Assertions.assertThat(distFile).exists();
        // distFile.deleteOnExit(); could be added to the test to clear test data after test execution
    }

}
