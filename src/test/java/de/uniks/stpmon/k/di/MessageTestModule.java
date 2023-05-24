package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.MessageApiDummy;
import de.uniks.stpmon.k.rest.MessageApiService;

@Module
public abstract class MessageTestModule {
    @Binds
    abstract MessageApiService messageApiService(MessageApiDummy dummy);
}
