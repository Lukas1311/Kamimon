package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public interface LoneRule {
    RuleResult apply(TileInfo current, List<DecorationLayer> layers);
}
