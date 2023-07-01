package de.uniks.stpmon.k.models;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;

public record Region(
        String _id,
        String name,
        Spawn spawn,
        TileMapData map
) implements IMapProvider {

}
