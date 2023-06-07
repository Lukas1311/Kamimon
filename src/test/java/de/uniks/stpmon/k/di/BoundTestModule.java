package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.dummies.FriendCacheDummy;
import de.uniks.stpmon.k.service.dummies.MovementDummy;
import de.uniks.stpmon.k.service.storage.IFriendCache;
import de.uniks.stpmon.k.service.world.IMovementService;

import javax.inject.Singleton;

@Module
public abstract class BoundTestModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCacheDummy cache);

    @Binds
    @Singleton
    public abstract IMovementService movementService(MovementDummy dummy);
}
