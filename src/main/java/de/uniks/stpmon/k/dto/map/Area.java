package de.uniks.stpmon.k.dto.map;

import de.uniks.stpmon.k.dto.IMapProvider;

public record Area(
        String _id,
        String name,
        String region,
        String createdAt,
        TileMap map,
        String updatedAt) implements IMapProvider {
}
