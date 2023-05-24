package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.storage.FriendCache;
import de.uniks.stpmon.k.service.storage.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCache cache);
}
