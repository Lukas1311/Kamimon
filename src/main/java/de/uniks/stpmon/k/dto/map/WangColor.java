package de.uniks.stpmon.k.dto.map;

/**
 * @param color       Hex-formatted color (#RRGGBB or #AARRGGBB)
 * @param name        Name of the Wang color
 * @param probability Probability used when randomizing
 * @param tile        Local ID of tile representing the Wang color
 */
public record WangColor(
        String color,
        String name,
        int probability,
        int tile
) {

}
