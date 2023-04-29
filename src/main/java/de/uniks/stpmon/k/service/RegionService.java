package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.rest.RegionApiService;

import javax.swing.plaf.synth.Region;
import java.util.List;

public class RegionService {
    private final RegionApiService regionApiService;

    //I wait of the UserModel!
    //private final User user;

    public RegionService(RegionApiService regionApiService) {
        this.regionApiService = regionApiService;
    }

    public List<Region> getRegions() {
        return null;
    }

    public Region getRegion(int id) {
        return null;
    }
}
