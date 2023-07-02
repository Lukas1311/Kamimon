package de.uniks.stpmon.k.world.rules;

public record TileInfo(
        int tileX,
        int tileY,
        int layer,
        int tileId,
        String tileSet
) {
}
