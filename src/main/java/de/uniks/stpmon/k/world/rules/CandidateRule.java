package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public interface CandidateRule {

    TileInfo apply(TileInfo current, List<TileInfo> candidates,
                   @SuppressWarnings("unused") List<DecorationLayer> layers);

}
