package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.awt.image.BufferedImage;
import java.util.List;

import static de.uniks.stpmon.k.constants.TileConstants.TILE_SIZE;

public class ImageConnectionRule implements ConnectionRule {

    public static final int CHECKED_PIXELS = 3;

    @Override
    public RuleResult apply(TileInfo current, TileInfo other,
                            Direction currentDir, Direction otherDir,
                            List<DecorationLayer> layers) {
        BufferedImage firstImage = layers.get(current.layer()).image();
        BufferedImage secondImage = layers.get(other.layer()).image();
        int emptyCount = 0;
        for (int dist = 0; dist < CHECKED_PIXELS; dist++) {
            // Iterate over each pixel in the images and compare the edges
            for (int i = 0; i < TILE_SIZE; i++) {
                int firstX = current.tileX() * TILE_SIZE + currentDir.imageX(i, dist);
                int firstY = current.tileY() * TILE_SIZE + currentDir.imageY(i, dist);
                int secondX = (current.tileX() + currentDir.tileX()) * TILE_SIZE + otherDir.imageX(i, dist);
                int secondY = (current.tileY() + currentDir.tileY()) * TILE_SIZE + otherDir.imageY(i, dist);
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
