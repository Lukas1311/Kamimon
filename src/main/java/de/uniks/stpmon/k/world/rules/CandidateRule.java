package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public interface CandidateRule {

    PropInfo apply(List<PropInfo> candidates, List<DecorationLayer> layers);

}
