package de.uniks.stpmon.k.dto;

import de.uniks.stpmon.k.dto.map.TileMap;

public record Region(
        String _id,
        String name,
        Spawn spawn,
        TileMap map
    ) implements IMapProvider {
}
