package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.*;

public class IncludedCandidateRule implements CandidateRule {
    private final Set<Integer> tileIds;
    private final String tileSet;

    public IncludedCandidateRule(String tileSet, int... c) {
        this(tileSet, Arrays.stream(c).boxed().toList());
    }

    public IncludedCandidateRule(String tileSet, Collection<Integer> c) {
        this.tileIds = new HashSet<>(c);
        this.tileSet = tileSet;
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
