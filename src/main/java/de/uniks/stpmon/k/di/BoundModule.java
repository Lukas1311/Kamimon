package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.storage.FriendCache;
import de.uniks.stpmon.k.service.storage.IFriendCache;
import de.uniks.stpmon.k.service.world.IMovementService;
import de.uniks.stpmon.k.service.world.MovementScheduler;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCache cache);

    @Binds
    @Singleton
    public abstract IMovementService movementService(MovementScheduler scheduler);
}
