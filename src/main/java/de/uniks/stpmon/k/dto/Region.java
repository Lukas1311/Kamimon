package de.uniks.stpmon.k.dto;

import de.uniks.stpmon.k.dto.map.TileMap;

public record Region(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        TileMap map
) implements IMapProvider {
}