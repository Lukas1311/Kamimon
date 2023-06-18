package de.uniks.stpmon.k.models.map.layerdata;

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
        int width,
        int height,
        int x,
        int y
) implements ITileDataProvider {
    @Override
    public int startx() {
        return x;
    }

    @Override
    public int starty() {
        return y;
    }

    public int getId(int x, int y) {
        int index = (y - starty()) * width() + (x - startx());
        return data().get(index);
    }
}
