package de.uniks.stpmon.k.dto;

import de.uniks.stpmon.k.dto.map.TileMapData;

public record Region(
        String _id,
        String name,
        Spawn spawn,
        TileMapData map
    ) implements IMapProvider {
}
