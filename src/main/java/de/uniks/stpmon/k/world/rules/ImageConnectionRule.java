package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageConnectionRule extends PropRule {

    public static final int CHECKED_PIXELS = 3;
    public static final int TILE_SIZE = 16;

    @Override
    public RuleResult apply(PropInfo info, List<DecorationLayer> layers) {
        BufferedImage firstImage = layers.get(info.layer()).image();
        BufferedImage secondImage = layers.get(info.otherLayer()).image();
        int emptyCount = 0;
        for (int dist = 0; dist < CHECKED_PIXELS; dist++) {
            // Iterate over each pixel in the images and compare the edges
            for (int i = 0; i < TILE_SIZE; i++) {
                int firstX = info.tileX() * TILE_SIZE + info.dir().imageX(i, dist);
                int firstY = info.tileY() * TILE_SIZE + info.dir().imageY(i, dist);
                int secondX = (info.tileX() + info.dir().tileX()) * TILE_SIZE + info.otherDir().imageX(i, dist);
                int secondY = (info.tileY() + info.dir().tileY()) * TILE_SIZE + info.otherDir().imageY(i, dist);
                int first = firstImage.getRGB(firstX, firstY);
                int second = secondImage.getRGB(secondX, secondY);
                int firstAlpha = (first >> 24 & 0xFF);
                int secondAlpha = (second >> 24 & 0xFF);
                // Skip if any pixel is transparent
                if (first == 0 || second == 0 || firstAlpha == 0 || secondAlpha == 0) {
                    emptyCount++;
                }
            }
            if (dist == 0 && emptyCount == TILE_SIZE) {
                return RuleResult.NO_MATCH;
            }
        }
        emptyCount /= CHECKED_PIXELS;
        // If more than 100% of the pixels are transparent, return false
        if (emptyCount >= TILE_SIZE) {
            return RuleResult.NO_MATCH;
        }

        return RuleResult.MATCH_CONNECTION;
    }
}