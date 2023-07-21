package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.List;

public class BottomExclusivelyRule implements ConnectionRule {

    private final RuleRegistry registry;

    public BottomExclusivelyRule(RuleRegistry registry) {
        this.registry = registry;
    }

    @Override
    public RuleResult apply(TileInfo current, TileInfo other,
                            Direction currentDir, Direction otherDir,
                            List<DecorationLayer> layers) {
        if (registry.isBottom(other) && currentDir == Direction.TOP) {
            return RuleResult.NO_MATCH_STOP;
        }
        if (registry.isBottom(current) && currentDir == Direction.BOTTOM) {
            return RuleResult.NO_MATCH_STOP;
        }
        return RuleResult.NO_MATCH;
    }
}
