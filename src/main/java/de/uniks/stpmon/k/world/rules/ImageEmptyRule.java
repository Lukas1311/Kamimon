package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.awt.image.BufferedImage;
import java.util.List;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

public class ImageEmptyRule implements LoneRule {
    public static final int RGB_EMPTY_THRESHOLD = 45;
    public static final int ALPHA_EMPTY_THRESHOLD = 10;

    @Override
    public RuleResult apply(TileInfo current, List<DecorationLayer> layers) {
        BufferedImage image = layers.get(current.layer()).image();
        for (int x = 0; x < TILE_SIZE; x++) {
            for (int y = 0; y < TILE_SIZE; y++) {

                int colorValue = image.getRGB(current.tileX() * TILE_SIZE + x,
                        current.tileY() * TILE_SIZE + y);
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
