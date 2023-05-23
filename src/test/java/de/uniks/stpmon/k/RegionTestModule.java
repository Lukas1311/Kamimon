package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Module
public class    RegionTestModule {

    @Provides
    @Singleton
    static RegionApiService regionApiService(){
        return new RegionApiService() {

            final List<Region> regions = new ArrayList<>();

            /**
             * Adds 2 DummyRegions to the regions list with ids {"id0", "id1"} and
             * names {"TestRegion0", "TestRegion"}
             */
            private void initDummyRegions(){
                Region region0 = new Region("2023-01-01T00:00:00.000Z",
                        "2023-02-02T00:00:00.000Z", "id0", "TestRegion0");

                Region region1 = new Region("2023-01-01T00:00:00.000Z",
                        "2023-02-02T00:00:00.000Z", "id1", "TestRegion1");

                regions.add(region0);
                regions.add(region1);

            }

            /**
             * Returns all regions (if list of regions is empty, it gets initialized
             */
            @Override
            public Observable<List<Region>> getRegions() {
                if(regions.isEmpty()){
                    initDummyRegions();
                }
                return Observable.just(regions);
            }

            /**
             * Returns all region with id (if list of regions is empty, it gets initialized
             */
            @Override
            public Observable<Region> getRegion(String id) {
                if(regions.isEmpty()){
                    initDummyRegions();
                }

                Optional<Region> region = regions.stream().filter(r -> r._id().equals(id)).findFirst();
                return region.map(r -> Observable.just(region.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }
        };
    }

}
