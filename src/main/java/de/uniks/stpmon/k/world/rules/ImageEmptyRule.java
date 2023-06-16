package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.world.PropInspector;

import java.awt.image.BufferedImage;

public class ImageEmptyRule extends PropRule {
    public static final int RGB_EMPTY_THRESHOLD = 45;
    public static final int ALPHA_EMPTY_THRESHOLD = 10;

    @Override
    public RuleResult apply(PropInfo info, BufferedImage image) {
        for (int x = 0; x < PropInspector.TILE_SIZE; x++) {
            for (int y = 0; y < PropInspector.TILE_SIZE; y++) {

                int colorValue = image.getRGB(info.tileX() * PropInspector.TILE_SIZE + x, info.tileY() * PropInspector.TILE_SIZE + y);
                int firstAlpha = (colorValue >> 24 & 0xFF);
                if (firstAlpha < ALPHA_EMPTY_THRESHOLD) {
                    continue;
                }
                // Calculate the intensity of each pixel
                double intensity = (firstAlpha +
                        (colorValue >> 16 & 0xFF) +
                        (colorValue >> 8 & 0xFF) +
                        ((colorValue) & 0xFF)) / 4.0;
                if (intensity < RGB_EMPTY_THRESHOLD) {
                    continue;
                }
                return RuleResult.MATCH_SINGLE;
            }
        }
        return RuleResult.NO_MATCH;
    }
}
