package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.utils.Direction;

public record PropInfo(
        int tileX,
        int tileY,
        int tileId,
        int otherTileId,
        String tileSet,
        Direction dir,
        Direction otherDir) {
}
