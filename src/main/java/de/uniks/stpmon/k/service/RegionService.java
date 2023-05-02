package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.dto.Region;
import java.util.List;

public class RegionService {
    private final RegionApiService regionApiService;

    public RegionService(RegionApiService regionApiService) {
        this.regionApiService = regionApiService;
    }

    public List<Region> getRegions() {
        return (List<Region>) regionApiService.getRegions();
    }

    public Region getRegion(int id) {
        return null;
        //TODO: return regionApiService.getRegion(String.valueOf(id));
    }
}
