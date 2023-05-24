package de.uniks.stpmon.k;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.FriendCacheDummy;
import de.uniks.stpmon.k.service.storages.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundTestModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCacheDummy cache);
}
