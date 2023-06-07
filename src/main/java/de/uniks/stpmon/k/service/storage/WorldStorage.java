package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.service.world.WorldSet;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldStorage {
    private WorldSet world;

    @Inject
    public WorldStorage() {
    }

    public void setWorld(WorldSet world) {
        this.world = world;
    }

    public WorldSet getWorld() {
        return world;
    }

    public boolean isEmpty() {
        return world == null;
    }
}
