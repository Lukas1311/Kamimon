package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.FriendCacheDummy;
import de.uniks.stpmon.k.service.storage.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundTestModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCacheDummy cache);
}
