package de.uniks.stpmon.k.world.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseTilesetRule {

    protected final String tileSet;
    protected final Set<Integer> tileIds;

    public BaseTilesetRule(String tileSet, Integer... tileIds) {
        this(tileSet, Arrays.asList(tileIds));
    }

    public BaseTilesetRule(String tileSet, Collection<Integer> c) {
        this.tileIds = new HashSet<>(c);
        this.tileSet = tileSet;
    }

    public BaseTilesetRule(String tileSet, IdSource... sources) {
        this(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    public Set<Integer> getTileIds() {
        return tileIds;
    }

    public String getTileSet() {
        return tileSet;
    }
}
