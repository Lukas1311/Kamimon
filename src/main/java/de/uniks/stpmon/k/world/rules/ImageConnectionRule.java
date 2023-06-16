package de.uniks.stpmon.k.world.rules;

import java.awt.image.BufferedImage;

public class ImageConnectionRule extends PropRule {

    public static final int RGB_THRESHOLD = 45;
    public static final int CONNECT_THRESHOLD = 5;
    public static final int CHECKED_PIXELS = 3;
    public static final int TILE_SIZE = 16;

    @Override
    public RuleResult apply(PropInfo info, BufferedImage image) {
        int meetThresholds = 0;
        int emptyCount = 0;
        for (int dist = 0; dist < CHECKED_PIXELS; dist++) {
            // Iterate over each pixel in the images and compare the edges
            for (int i = 0; i < TILE_SIZE; i++) {
                int firstX = info.tileX() * TILE_SIZE + info.dir().imageX(i, dist);
                int firstY = info.tileY() * TILE_SIZE + info.dir().imageY(i, dist);
                int secondX = (info.tileX() + info.dir().tileX()) * TILE_SIZE + info.otherDir().imageX(i, dist);
                int secondY = (info.tileY() + info.dir().tileY()) * TILE_SIZE + info.otherDir().imageY(i, dist);
                int first = image.getRGB(firstX, firstY);
                int second = image.getRGB(secondX, secondY);
                int firstAlpha = (first >> 24 & 0xFF);
                int secondAlpha = (second >> 24 & 0xFF);
                // Skip if any pixel is transparent
                if (first == 0 || second == 0 || firstAlpha == 0 || secondAlpha == 0) {
                    emptyCount++;
                    continue;
                }

                // Calculate the grayscale intensity of each pixel
                double intensity1 = (firstAlpha +
                        (first >> 16 & 0xFF) +
                        (first >> 8 & 0xFF) +
                        ((first) & 0xFF)) / 4.0;
                double intensity2 = (secondAlpha +
                        (second >> 16 & 0xFF) +
                        (second >> 8 & 0xFF) +
                        ((second) & 0xFF)) / 4.0;
                // Compare the intensities and check if the difference is above the threshold
                if (Math.abs(intensity1 - intensity2) <= RGB_THRESHOLD) {
                    meetThresholds += 1;
                }
            }
            if (dist == 0 && emptyCount == TILE_SIZE) {
                return RuleResult.NO_MATCH;
            }
        }
        meetThresholds /= CHECKED_PIXELS;
        emptyCount /= CHECKED_PIXELS;
        // If more than 100% of the pixels are transparent, return false
        if (emptyCount >= TILE_SIZE) {
            return RuleResult.NO_MATCH;
        }

        return RuleResult.MATCH_CONNECTION;
    }
}
