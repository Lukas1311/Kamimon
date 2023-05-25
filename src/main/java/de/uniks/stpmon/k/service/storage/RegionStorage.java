package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Region;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegionStorage {

    private Region region;

    @Inject
    public RegionStorage() {
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
