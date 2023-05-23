package de.uniks.stpmon.k.dto.map;

import java.util.List;

/**
 * @param compressionlevel The compression level to use for tile layer data (defaults to -1, which means to use the algorithm default)
 * @param height           Number of tile rows
 * @param infinite         Whether the map has infinite dimensions
 * @param layers           Array of Layers
 * @param nextlayerid      Auto-increments for each layer
 * @param nextobjectid     Auto-increments for each placed object
 * @param orientation      orthogonal, isometric, staggered or hexagonal
 * @param renderorder      right-down (the default), right-up, left-down or left-up (currently only supported for orthogonal maps)
 * @param tiledversion     The Tiled version used to save the file
 * @param tileheight       Map grid height
 * @param tilesets         Array of Tilesets
 * @param tilewidth        Map grid width
 * @param type             map
 * @param version          The JSON format version (previously a number, saved as string since 1.6)
 * @param width            Number of tile columns
 */
@SuppressWarnings("SpellCheckingInspection")
public record TileMap(
        int compressionlevel,
        int height,
        boolean infinite,
        List<TileLayer> layers,
        int nextlayerid,
        int nextobjectid,
        String orientation,
        String renderorder,
        String tiledversion,
        int tileheight,
        List<Tileset> tilesets,
        int tilewidth,
        String type,
        String version,
        int width
) {

}
