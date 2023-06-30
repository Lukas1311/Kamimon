package de.uniks.stpmon.k.models.map.layerdata;

import de.uniks.stpmon.k.models.map.Property;

import java.util.List;

/**
 * @param chunks     Array of chunks (optional). tilelayer only. Available in layer index = 0 = 1
 * @param objects    Array of objects. contains textual data like route texts and more. Available in layer index = 2
 * @param height     Row count. Same as map height for fixed-size maps. tilelayer only.
 * @param id         Incremental ID - unique across all layers
 * @param name       Name assigned to this layer
 * @param properties Array of Properties
 * @param startx     X coordinate where layer content starts (for infinite maps)
 * @param starty     Y coordinate where layer content starts (for infinite maps)
 * @param type       tilelayer, objectgroup, imagelayer or group
 * @param visible    Whether layer is shown or hidden in editor
 * @param width      Column count. Same as map width for fixed-size maps. tilelayer only.
 * @param x          Horizontal layer offset in tiles. Always 0.
 * @param y          Vertical layer offset in tiles. Always 0.
 */
@SuppressWarnings("SpellCheckingInspection")
public record TileLayerData(
        int id,
        String name,
        List<ChunkData> chunks,
        List<Integer> data,
        List<ObjectData> objects,
        int x,
        int y,
        int width,
        int height,
        int startx,
        int starty,
        String type,
        boolean visible,
        List<Property> properties
) implements ITileDataProvider {
    public static final String GROUND_TYPE = "Ground";
    public static final String WALLS_TYPE = "Walls";


    public boolean checkBounds(int x, int y) {
        return x >= startx() && x < startx() + width() && y >= starty() && y < starty() + height();
    }

    public int getIdFromData(int x, int y) {
        int index = (x - startx()) + (y - starty()) * (width());
        return data().get(index);
    }
}
