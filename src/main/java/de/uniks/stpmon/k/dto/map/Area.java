package de.uniks.stpmon.k.dto.map;

public record Area(
        String _id,
        String name,
        String region,
        String createdAt,
        TileMap map,
        String updatedAt) {
}
