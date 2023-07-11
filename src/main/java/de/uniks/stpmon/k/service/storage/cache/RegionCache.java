package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RegionCache extends SimpleCache<Region, String> {

    @Inject
    RegionApiService regionApiService;

    @Inject
    public RegionCache() {
    }

    @Override
    protected Observable<List<Region>> getInitialValues() {
        return regionApiService.getRegions();
    }

    @Override
    public String getId(Region value) {
        return value._id();
    }
}
