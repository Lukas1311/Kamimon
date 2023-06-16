package de.uniks.stpmon.k.models.map;

import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;

import java.util.Comparator;
import java.util.List;

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

    public TilesetSource getTileset(int id) {
        return tilesets.stream()
                .sorted(Comparator.comparingInt(TilesetSource::firstgid).reversed())
                .filter(tileset -> (id - tileset.firstgid()) >= 0)
                .findFirst()
                .orElse(null);
    }

    public int getId(int x, int y, int z) {
        if (z < 0 || z >= layers.size()) {
            return -1;
        }
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return -1;
        }
        TileLayerData layer = layers.get(z);
        if (x < layer.startx() || x >= layer.startx() + layer.width() || y < layer.starty() || y >= layer.starty() + layer.height()) {
            return -1;
        }
        if (layer.width() < width || layer.height() < height) {
            return -1;
        }

        List<ChunkData> chunks = layer.chunks();
        int index = (int) Math.floor((x - layer.startx()) / 16f) + (int) Math.floor((y - layer.starty()) / 16f) * (width / 16);
        // fewer chunks than expected, probably all empty
        if (index < 0 || index >= chunks.size()) {
            return -1;
        }
        ChunkData chunk = chunks.get(index);
        int id = (y - chunk.y()) * chunk.width() + (x - chunk.x());
        return chunk.data().get(id);
    }
}
