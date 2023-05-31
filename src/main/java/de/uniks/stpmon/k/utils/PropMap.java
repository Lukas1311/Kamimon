package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.dto.IMapProvider;

import java.util.ArrayList;
import java.util.List;

public class PropMap {
    private final List<TileProp> props = new ArrayList<>();
    private final PropInspector inspector;

    public PropMap(IMapProvider provider) {
        this.inspector = new PropInspector(provider.map());
    }
}
