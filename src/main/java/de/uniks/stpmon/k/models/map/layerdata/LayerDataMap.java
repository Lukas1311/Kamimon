package de.uniks.stpmon.k.models.map.layerdata;

import java.util.List;

/**
 * @param chunks     Array of chunks (optional). tilelayer only.
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
public record LayerDataMap(
        int id,
        String name,
        List<ChunkData> chunks,
        int x,
        int y,
        int width,
        int height,
        int startx,
        int starty,
        String type,
        boolean visible,
        List<Property> properties
) implements ITileLayerData {}
