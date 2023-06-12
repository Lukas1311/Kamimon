package de.uniks.stpmon.k.world.rules;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExtractionRule extends PropRule {
    private final Set<Integer> tileIds;

    public ExtractionRule(Integer... tileIds) {
        this(Arrays.asList(tileIds));
    }

    public ExtractionRule(Collection<Integer> c) {
        this.tileIds = new HashSet<>(c);
    }

    @Override
    public RuleResult apply(PropInfo info, BufferedImage image) {
        return null;
    }
}
