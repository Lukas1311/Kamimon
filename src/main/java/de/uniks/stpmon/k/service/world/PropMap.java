package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TileProp;

import java.util.List;

public class PropMap {
    private final PropInspector inspector;
    private final TileMap tileMap;

    public PropMap(TileMap tileMap) {
        TileMapData data = tileMap.getData();
        this.inspector = new PropInspector(data.width(), data.height());
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
