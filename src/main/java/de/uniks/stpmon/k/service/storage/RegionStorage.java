package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegionStorage {

    private Region region;
    private Area area;

    @Inject
    public RegionStorage() {
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

    public boolean isEmpty() {
        return region == null || area == null;
    }
}
