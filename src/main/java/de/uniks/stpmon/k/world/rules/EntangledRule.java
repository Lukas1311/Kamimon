package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public class EntangledRule extends BaseTilesetRule implements PropRule {

    public EntangledRule(String tileSet, IdSource... sources) {
        super(tileSet, sources);
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
