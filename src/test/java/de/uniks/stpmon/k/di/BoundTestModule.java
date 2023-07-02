package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.dummies.DummyResourceService;
import de.uniks.stpmon.k.service.dummies.FriendCacheDummy;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundTestModule {

    @Binds
    @Singleton
    @SuppressWarnings("unused")
    public abstract IFriendCache friendCache(FriendCacheDummy cache);

    @Binds
    @Singleton
    @SuppressWarnings("unused")
    public abstract IResourceService resourceService(DummyResourceService service);

    @Binds
    @IntoSet
    @SuppressWarnings("unused")
    public abstract ILifecycleService cacheManager(CacheManager manager);

}
