package de.uniks.stpmon.k.dto.map;

import java.util.List;

/**
 * @param data   Array of unsigned int (GIDs) or base64-encoded data. tilelayer only.
 * @param height Height in tiles
 * @param width  Width in tiles
 * @param x      X coordinate in tiles
 * @param y      Y coordinate in tiles
 */
public record ChunkData(
        List<Integer> data,
        int height,
        int width,
        int x,
        int y
) {
}
