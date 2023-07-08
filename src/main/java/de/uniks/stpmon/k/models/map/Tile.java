package de.uniks.stpmon.k.models.map;

import java.util.List;

/**
 * @param id         Local ID of the tile
 * @param properties Array of Properties
 */
@SuppressWarnings("unused")
public record Tile(int id, List<Property> properties) {

}
