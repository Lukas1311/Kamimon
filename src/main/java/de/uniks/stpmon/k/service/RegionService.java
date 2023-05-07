package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.dto.Region;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import javax.inject.Inject;

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
}
