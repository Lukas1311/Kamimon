package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RegionService {
    private final RegionApiService regionApiService;

    @Inject
    protected RegionStorage regionStorage;

    @Inject
    public RegionService(RegionApiService regionApiService) {
        this.regionApiService = regionApiService;
    }

    public Observable<List<Region>> getRegions() {
        return regionApiService.getRegions();
    }

    public Observable<Region> getRegion(String id) {
        return regionApiService.getRegion(id);
    }

    public Observable<Area> enterRegion(Region region) {
        if (region.spawn() == null) {
            return Observable.error(new Exception("Region has no spawn."));
        }
        if (region.spawn().area() == null) {
            return Observable.error(new Exception("Spawn has no area."));
        }
        return getArea(region._id(), region.spawn().area()).map(area -> {
            regionStorage.setRegion(region);
            regionStorage.setArea(area);
            return area;
        });
    }

    public Observable<Area> getArea(String regionId, String areaId) {
        return regionApiService.getArea(regionId, areaId);
    }

    public Observable<List<Area>> getAreas(String regionId) {
        return regionApiService.getAreas(regionId);
    }
}
