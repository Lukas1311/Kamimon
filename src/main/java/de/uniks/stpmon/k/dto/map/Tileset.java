package de.uniks.stpmon.k.dto.map;

/**
 * @param firstgid GID corresponding to the first tile in the set
 * @param source   The external file that contains this tilesets data
 */
@SuppressWarnings("SpellCheckingInspection")
public record Tileset(
        int firstgid,

        String source) {

}
