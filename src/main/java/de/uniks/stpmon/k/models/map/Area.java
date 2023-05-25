package de.uniks.stpmon.k.models.map;

import de.uniks.stpmon.k.dto.IMapProvider;

public record Area(
        String _id,
        String name,
        String region,
        String createdAt,
        TileMapData map,
        String updatedAt) implements IMapProvider {
}
