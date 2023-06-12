package de.uniks.stpmon.k.models.map;

import java.util.List;

import de.uniks.stpmon.k.models.map.layerdata.ITileLayerData;

/**
 * @param height           Number of tile rows
 * @param infinite         Whether the map has infinite dimensions
 * @param layers           Array of Layers
 * @param tileheight       Map grid height
 * @param tilesets         Array of Tilesets
 * @param tilewidth        Map grid width
 * @param type             map
 * @param width            Number of tile columns
 */
@SuppressWarnings("SpellCheckingInspection")
public record TileMapData(
        int width,
        int height,
        boolean infinite,
        List<ITileLayerData> layers,
        int tilewidth,
        int tileheight,
        List<TilesetSource> tilesets,
        String type
) {

}
