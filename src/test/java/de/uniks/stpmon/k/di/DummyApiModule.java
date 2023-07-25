package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.rest.*;
import de.uniks.stpmon.k.service.dummies.*;

@Module
@SuppressWarnings("unused")
public abstract class DummyApiModule {

    @Binds
    abstract RegionApiService regionApiService(RegionApiDummy regionApiDummy);

    @Binds
    abstract PresetApiService presetApiService(PresetApiDummy dummyPresetService);

    @Binds
    abstract GroupApiService groupApiService(GroupApiDummy dummyGroupService);

    @Binds
    abstract MessageApiService messageApiService(MessageApiDummy dummy);

    @Binds
    abstract AuthenticationApiService authApiService(AuthenticationApiDummy authenticationApiDummy);

    @Binds
    abstract EncounterApiService encounterApiService(EncounterApiDummy dummy);

    @Binds
    abstract TrainerItemApiService trainerItemApiService(TrainerItemApiDummy trainerItemApiDummy);

}
