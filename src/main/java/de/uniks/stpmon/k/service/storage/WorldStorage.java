package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.utils.TileMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldStorage {
    public TileMap tileMap;

    @Inject
    public WorldStorage() {

    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
