package de.uniks.stpmon.k.models.map.layerdata;

import de.uniks.stpmon.k.constants.TileConstants;

import java.util.List;

public interface ITileDataProvider {

    List<Long> data();

    int width();

    int height();

    int startx();

    int starty();

    /**
     * Get the global id from the local coordinates relative to the chunk.
     *
     * @param x x coordinate relative to the chunk (0-15)
     * @param y y coordinate relative to the chunk (0-15)
     * @return global id of the tile (unsigned int 32 bit)
     */
    default long getGlobalIdFromLocal(int x, int y) {
        int index = x + y * width();
        return data().get(index);
    }

    /**
     * Get the tile id from the local coordinates relative to the chunk.
     *
     * @param x x coordinate relative to the chunk (0-15)
     * @param y y coordinate relative to the chunk (0-15)
     * @return tile id of the tile (unsigned int 28 bit)
     */
    @SuppressWarnings("unused")
    default int getTileIdFromLocal(int x, int y) {
        return (int) (getGlobalIdFromLocal(x, y) & TileConstants.TILE_ID_MASK);
    }

    /**
     * Get the global id from the global coordinates.
     *
     * @param x x coordinate relative to the map
     * @param y y coordinate relative to the map
     * @return global id of the tile (unsigned int 32 bit)
     */
    default long getGlobalId(int x, int y) {
        return getGlobalIdFromLocal(x - startx(), y - starty());
    }

    /**
     * Get the tile id from the global coordinates.
     *
     * @param x x coordinate relative to the map
     * @param y y coordinate relative to the map
     * @return tile id of the tile (unsigned int 28 bit)
     */
    default int getTileId(int x, int y) {
        return (int) (getGlobalId(x, y) & TileConstants.TILE_ID_MASK);
    }
}
