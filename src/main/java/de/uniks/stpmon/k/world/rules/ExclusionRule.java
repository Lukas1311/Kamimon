package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.*;

public class ExclusionRule extends PropRule {

    private final String tileSet;
    private final Set<Integer> tileIds;

    public ExclusionRule(String tileSet, Integer... tileIds) {
        this(tileSet, Arrays.asList(tileIds));
    }

    public ExclusionRule(String tileSet, Collection<Integer> c) {
        this.tileIds = new HashSet<>(c);
        this.tileSet = tileSet;
    }

    public ExclusionRule(String tileSet, IdSource... sources) {
        this(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    @Override
    public RuleResult apply(PropInfo info, List<DecorationLayer> layers) {
        if (!tileSet.equals(info.tileSet())) {
            return RuleResult.NO_MATCH;
        }
        if (tileIds.contains(info.tileId())) {
            return RuleResult.NO_MATCH_DECORATION;
        }
        if (tileIds.contains(info.otherTileId())) {
            return RuleResult.NO_MATCH_DECORATION;
        }
        return RuleResult.NO_MATCH;
    }

}
