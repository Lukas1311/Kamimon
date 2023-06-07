package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.storage.FriendCache;
import de.uniks.stpmon.k.service.storage.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
    @Binds
    @Singleton
    public abstract IFriendCache friendCache(FriendCache cache);

    @Binds
    @Singleton
    public abstract IResourceService resourceService(ResourceService service);
}
