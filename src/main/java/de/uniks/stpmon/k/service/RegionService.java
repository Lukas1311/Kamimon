package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class RegionService {
    private final RegionApiService regionApiService;

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

    public Observable<Area> getArea(String regionId, String areaId) {
        return regionApiService.getArea(regionId, areaId);
    }

    public Observable<List<Area>> getAreas(String regionId) {
        return regionApiService.getAreas(regionId);
    }
}
