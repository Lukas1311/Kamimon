package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public class IncludedCandidateRule extends BaseTilesetRule implements CandidateRule {

    public IncludedCandidateRule(String tileSet, Integer... tileIds) {
        super(tileSet, tileIds);
    }

    @Override
    public PropInfo apply(List<PropInfo> candidates, List<DecorationLayer> layers) {
        for (PropInfo candidate : candidates) {
            if (!tileSet.equals(candidate.tileSet())) {
                continue;
            }
            boolean first = tileIds.contains(candidate.otherTileId());
            boolean second = tileIds.contains(candidate.tileId());
            // Booth tiles have to be in the set
            if (first && second) {
                return candidate;
            }
        }
        return null;
    }
}
