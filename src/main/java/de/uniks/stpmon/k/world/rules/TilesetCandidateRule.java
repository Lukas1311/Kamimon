package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.List;

public class TilesetCandidateRule implements CandidateRule {

    private final String tileset;
    private final int distance;
    private final int columns;

    private final int offset;

    public TilesetCandidateRule(String tileset, int columns) {
        this(tileset, columns, 1, 0);
    }

    public TilesetCandidateRule(String tileset, int columns, int offset) {
        this(tileset, columns, 1, offset);
    }

    public TilesetCandidateRule(String tileset, int columns, int distance, int offset) {
        this.tileset = tileset;
        this.distance = distance;
        this.columns = columns;
        this.offset = offset;
    }

    @Override
    public TileInfo apply(TileInfo current, List<TileInfo> candidates, List<DecorationLayer> layers, Direction dir) {
        if (!current.tileSet().equals(tileset)) {
            return null;
        }
        int id = current.tileId() - offset;
        for (TileInfo candidate : candidates) {
            if (!candidate.tileSet().equals(tileset)) {
                continue;
            }
            int otherId = candidate.tileId() - offset;
            int mainX = id % columns;
            int mainY = id / columns;
            int otherX = otherId % columns;
            int otherY = otherId / columns;
//            int diffX = mainX - otherX;
//            int diffY = mainY - otherY;
//            switch (dir) {
//                case LEFT -> {
//                    if (diffX > 0) {
//                        continue;
//                    }
//                }
//                case RIGHT -> {
//                    if (diffX < 0) {
//                        continue;
//                    }
//                }
//                case TOP -> {
//                    if (diffY < 0) {
//                        continue;
//                    }
//                }
//                case BOTTOM -> {
//                    if (diffY > 0) {
//                        continue;
//                    }
//                }
//            }
            int diff = Math.abs(mainX - otherX) + Math.abs(mainY - otherY);
            if (diff > distance) {
                continue;
            }
            return candidate;
        }
        return null;
    }

}
