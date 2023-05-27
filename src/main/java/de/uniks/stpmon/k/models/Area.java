package de.uniks.stpmon.k.models;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;

public record Area(
        String _id, // objectid example: 507f191e810c19729de860ea
        String region,
        String name, //  minLength: 1, maxLength: 32
        TileMapData map
) implements IMapProvider {

}
