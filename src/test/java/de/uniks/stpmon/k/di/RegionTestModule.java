package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.rest.RegionApiService;


@Module
public abstract class RegionTestModule {

    @Binds
    abstract RegionApiService regionApiService(RegionApiService dummy);
}
