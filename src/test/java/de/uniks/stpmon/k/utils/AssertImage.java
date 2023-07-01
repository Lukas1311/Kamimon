package de.uniks.stpmon.k.utils;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertImage {

    public static void assertEqualsImages(BufferedImage image1, BufferedImage image2) {
        assertTrue(compareImages(image1, image2));
    }

    private static boolean compareImages(BufferedImage image1, BufferedImage image2) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        if (width != image2.getWidth() || height != image2.getHeight()) {
            return false;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = image1.getRGB(x, y);
                int rgb2 = image2.getRGB(x, y);

                if (rgb1 != rgb2) {
                    return false;
                }
            }
        }

        return true;
    }

}
