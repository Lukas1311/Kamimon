package de.uniks.stpmon.k.di;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerManager;
import de.uniks.stpmon.k.service.cache.CacheProxy;
import de.uniks.stpmon.k.service.cache.ICache;
import de.uniks.stpmon.k.service.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.FriendCache;
import de.uniks.stpmon.k.service.storage.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
    @Binds
    @Singleton
    @SuppressWarnings("unused")
    public abstract IFriendCache friendCache(FriendCache cache);

    @Binds
    @Singleton
    @SuppressWarnings("unused")
    public abstract IResourceService resourceService(ResourceService service);

    @Binds
    @IntoSet
    public abstract ILifecycleService trainerManager(TrainerManager manager);

    @Binds
    @Singleton
    public abstract ICache<Monster> monsterCache(CacheProxy<MonsterCache, Monster> cache);
}
