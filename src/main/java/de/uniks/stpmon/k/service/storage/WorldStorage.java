package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.utils.World;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorldStorage {
    public World world;

    @Inject
    public WorldStorage() {

    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public boolean isEmpty() {
        return world == null;
    }
}
