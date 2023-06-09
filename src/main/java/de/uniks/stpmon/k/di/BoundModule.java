package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.FriendCache;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
    @Binds
    @SuppressWarnings("unused")
    public abstract IFriendCache friendCache(FriendCache cache);

    @Binds
    @Singleton
    @SuppressWarnings("unused")
    public abstract IResourceService resourceService(ResourceService service);

    @Binds
    @IntoSet
    @SuppressWarnings("unused")
    public abstract ILifecycleService trainerManager(CacheManager manager);
}
