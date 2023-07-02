package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.List;

public interface ConnectionRule {

    RuleResult apply(TileInfo current, TileInfo other,
                     Direction currentDir, Direction otherDir,
                     List<DecorationLayer> layers);
}
