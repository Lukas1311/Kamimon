package de.uniks.stpmon.k.models.map;

import java.util.List;

/**
 * @param columns     The number of tile columns in the tileset
 * @param image       Image used for tiles in this set
 * @param imageheight Height of source image in pixels
 * @param imagewidth  Width of source image in pixels
 * @param margin      Buffer between image edge and first tile (pixels)
 * @param name        Name given to this tileset
 * @param spacing     Spacing between adjacent tiles in image (pixels)
 * @param tilecount   The number of tiles in this tileset
 * @param tileheight  Maximum height of tiles in this set
 * @param tiles       Array of Tiles (optional)
 * @param tilewidth   Maximum width of tiles in this set
 * @param type        tileset (for tileset files, since 1.0)
 */
public record TilesetData(
        int columns,
        String image,
        int imagewidth,
        int imageheight,
        int margin,
        String name,
        int spacing,
        int tilecount,
        int tilewidth,
        int tileheight,
        List<Tile> tiles,
        String type
) {

}
