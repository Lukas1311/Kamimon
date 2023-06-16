package de.uniks.stpmon.k.models.map;

import java.util.List;

import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

/**
 * @param height     Number of tile rows
 * @param infinite   Whether the map has infinite dimensions
 * @param layers     Array of Layers
 * @param tileheight Map grid height
 * @param tilesets   Array of Tilesets
 * @param tilewidth  Map grid width
 * @param type       map
 * @param width      Number of tile columns
 */
@SuppressWarnings("SpellCheckingInspection")
public record TileMapData(
        int width,
        int height,
        boolean infinite,
        List<TileLayerData> layers,
        int tilewidth,
        int tileheight,
        List<TilesetSource> tilesets,
        String type
) {

    public int getId(int x, int y, int z) {
        if (z < 0 || z >= layers.size()) {
            return -1;
        }
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return -1;
        }
        TileLayerData layer = layers.get(z);
        List<ChunkData> chunks = layer.chunks();
        ChunkData chunk = chunks.get((int) Math.floor(x / 16f) + (int) Math.floor(y / 16f) * (width / 16));
        return chunk.data().get((y - chunk.height()) * chunk.width() / 16 + (x - chunk.x()));
    }
}
