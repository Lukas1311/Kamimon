package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.*;

public class EntangledRule extends PropRule {

    private final String tileSet;
    private final Set<Integer> tileIds;

    public EntangledRule(String tileSet, Collection<Integer> c) {
        this.tileIds = new HashSet<>(c);
        this.tileSet = tileSet;
    }

    public EntangledRule(String tileSet, IdSource... sources) {
        this(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    @Override
    public RuleResult apply(PropInfo info, List<DecorationLayer> layers) {
        if (!tileSet.equals(info.tileSet())) {
            return RuleResult.NO_MATCH;
        }
        boolean first = tileIds.contains(info.otherTileId());
        boolean second = tileIds.contains(info.tileId());
        // Booth tiles have to be in the set
        if (!first && second || first && !second) {
            return RuleResult.NO_MATCH_STOP;
        }
        // both true
        if (second) {
            return RuleResult.MATCH_CONNECTION;
        }
        return RuleResult.NO_MATCH;
    }


}
