package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.utils.Direction;

public record PropInfo(
        int tileX,
        int tileY,
        int layer,
        int tileId,
        int otherTileId,
        int otherLayer,
        String tileSet,
        String otherTileSet,
        Direction dir,
        Direction otherDir) {
}
