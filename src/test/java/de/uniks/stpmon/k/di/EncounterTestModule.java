package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.dummies.EncounterApiDummy;

@Module
public abstract class EncounterTestModule {

    @Binds
    @SuppressWarnings("unused")
    abstract EncounterApiService encounterApiService(EncounterApiDummy dummy);

}
