package de.uniks.stpmon.k;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.rest.MessageApiService;

@Module
public abstract class MessageTestModule {
    @Binds
    abstract MessageApiService messageApiService(MessageApiDummy dummy);
}
