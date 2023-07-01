package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public class ExclusionRule extends BaseTilesetRule implements PropRule {

    public ExclusionRule(String tileSet, Integer... tileIds) {
        super(tileSet, tileIds);
    }

    public ExclusionRule(String tileSet, IdSource... sources) {
        super(tileSet, sources);
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
