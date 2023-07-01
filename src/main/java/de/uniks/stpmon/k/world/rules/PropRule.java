package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;

import java.util.List;

public abstract class PropRule {

    public abstract RuleResult apply(PropInfo info, List<DecorationLayer> layers);

}
