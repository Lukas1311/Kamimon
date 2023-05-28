package de.uniks.stpmon.k.models.map;

/**
 * @param firstgid GID corresponding to the first tile in the set
 * @param source   The external file that contains this tilesets data
 */
@SuppressWarnings("SpellCheckingInspection")
public record TilesetSource(
        int firstgid,

        String source) {

}
