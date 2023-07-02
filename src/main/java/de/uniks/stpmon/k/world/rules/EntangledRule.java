package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.List;

public class EntangledRule implements ConnectionRule {

    private final RuleRegistry registry;

    public EntangledRule(RuleRegistry registry) {
        this.registry = registry;
    }

    @Override
    public RuleResult apply(TileInfo current, TileInfo other, Direction currentDir, Direction otherDir, List<DecorationLayer> layers) {
        int first = registry.getEntangledGroup(current.tileSet(), current.tileId());
        int second = registry.getEntangledGroup(other.tileSet(), other.tileId());
        // If groups are not the same, stop
        if (first != second) {
            return RuleResult.NO_MATCH_STOP;
        }
        // if first is positive, booth have a group that is the same, we have a match
        if (first > 0) {
            return RuleResult.MATCH_CONNECTION;
        }
        return RuleResult.NO_MATCH;
    }
}
