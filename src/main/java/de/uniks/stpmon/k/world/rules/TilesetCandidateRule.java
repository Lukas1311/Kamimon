package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public class TilesetCandidateRule implements CandidateRule {

    private final String tileset;
    private final int distance;
    private final int columns;

    public TilesetCandidateRule(String tileset, int columns) {
        this(tileset, columns, 1);
    }

    public TilesetCandidateRule(String tileset, int columns, int distance) {
        this.tileset = tileset;
        this.distance = distance;
        this.columns = columns;
    }

    @Override
    public PropInfo apply(List<PropInfo> candidates, List<DecorationLayer> layers) {
        for (PropInfo candidate : candidates) {
            if (!candidate.tileSet().equals(tileset)
                    || !candidate.otherTileSet().equals(tileset)) {
                continue;
            }
            int id = candidate.tileId();
            int otherId = candidate.otherTileId();
            int mainX = id % columns;
            int mainY = id / columns;
            int otherX = otherId % columns;
            int otherY = otherId / columns;
            int diff = Math.abs(mainX - otherX) + Math.abs(mainY - otherY);
            if (diff > distance) {
                continue;
            }
            return candidate;
        }
        return null;
    }

}
