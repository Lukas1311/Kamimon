package de.uniks.stpmon.k.utils;

import java.util.List;

public class PropMap {
    private final PropInspector inspector;
    private final TileMap tileMap;

    public PropMap(TileMap tileMap) {
        this.inspector = new PropInspector(tileMap.getData());
        this.tileMap = tileMap;
    }

    public List<TileProp> createProps() {
        if (tileMap.getLayers().size() < 2) {
            return List.of();
        }
        return inspector.work(tileMap
                .getLayers().get(1));
    }
}
