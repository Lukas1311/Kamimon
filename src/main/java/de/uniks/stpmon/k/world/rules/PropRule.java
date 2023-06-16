package de.uniks.stpmon.k.world.rules;

import java.awt.image.BufferedImage;

public abstract class PropRule {

    public abstract RuleResult apply(PropInfo info, BufferedImage image);
}
