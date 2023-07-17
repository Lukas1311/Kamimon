package de.uniks.stpmon.k.models.map.layerdata;

import de.uniks.stpmon.k.constants.TileConstants;

import java.util.List;

public interface ITileDataProvider {

    List<Long> data();

    int width();

    int height();

    int startx();

    int starty();

    default int getTileId(int x, int y) {
        return (int) (getGlobalIdFromLocal(x - startx(), y - starty()) & TileConstants.TILE_ID_MASK);
    }

    default long getGlobalIdFromLocal(int x, int y) {
        int index = x + y * width();
        return data().get(index);
    }

    default int getTileIdFromLocal(int x, int y) {
        return (int) (getGlobalIdFromLocal(x, y) & TileConstants.TILE_ID_MASK);
    }
}
