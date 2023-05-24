package de.uniks.stpmon.k.dto.map;

import java.util.List;

/**
 * @param columns      The number of tile columns in the tileset
 * @param image        Image used for tiles in this set
 * @param imageheight  Height of source image in pixels
 * @param imagewidth   Width of source image in pixels
 * @param margin       Buffer between image edge and first tile (pixels)
 * @param name         Name given to this tileset
 * @param spacing      Spacing between adjacent tiles in image (pixels)
 * @param tilecount    The number of tiles in this tileset
 * @param tiledversion The Tiled version used to save the file
 * @param tileheight   Maximum height of tiles in this set
 * @param tiles        Array of Tiles (optional)
 * @param tilewidth    Maximum width of tiles in this set
 * @param type         tileset (for tileset files, since 1.0)
 * @param version      The JSON format version (previously a number, saved as string since 1.6)
 * @param wangsets     Array of Wang sets (since 1.1.5)
 */
public record TilesetData(
        int columns,
        String image,
        int imageheight,
        int imagewidth,
        int margin,
        String name,
        int spacing,
        int tilecount,
        String tiledversion,
        int tileheight,
        List<Tile> tiles,
        int tilewidth,
        String type,
        String version,
        List<WangSet> wangsets
) {

}
