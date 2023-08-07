package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public class IncludedCandidateRule extends BaseTilesetRule implements CandidateRule {

    public IncludedCandidateRule(String tileSet, Integer... tileIds) {
        super(tileSet, tileIds);
    }

    public IncludedCandidateRule(String tileSet, IdSource... tileIds) {
        super(tileSet, tileIds);
    }

    @Override
    public TileInfo apply(TileInfo current, List<TileInfo> candidates, List<DecorationLayer> layers) {
        boolean first = tileIds.contains(current.tileId());
        for (TileInfo candidate : candidates) {
            if (!tileSet.equals(candidate.tileSet())) {
                continue;
            }
            boolean second = tileIds.contains(candidate.tileId());
            // Booth tiles have to be in the set
            if (first && second) {
                return candidate;
            }
        }
        return null;
    }
}
