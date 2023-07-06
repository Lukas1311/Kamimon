package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;

import javax.inject.Provider;

@SuppressWarnings("unused")
public class CacheManagerDummy extends CacheManager {

    public static void init(CacheManager cacheManager) {
        init(cacheManager, FriendCacheDummy::new);
    }

    public static void init(CacheManager cacheManager, Provider<IFriendCache> friendProvider) {
        CacheManagerDummy dummy = (CacheManagerDummy) cacheManager;
        dummy.setFriendCacheProvider(friendProvider);
    }

    public static void initTrainer(CacheManager cacheManager, Provider<TrainerCache> trainerProvider) {
        CacheManagerDummy dummy = (CacheManagerDummy) cacheManager;
        dummy.setTrainerCacheProvider(trainerProvider);
    }

    private void setFriendCacheProvider(Provider<IFriendCache> friendCacheProvider) {
        this.friendCacheProvider = friendCacheProvider;
    }

    private void setMonsterCacheProvider(Provider<MonsterCache> monsterCacheProvider) {
        this.monsterCacheProvider = monsterCacheProvider;
    }

    private void setTrainerCacheProvider(Provider<TrainerCache> trainerCacheProvider) {
        this.trainerCacheProvider = trainerCacheProvider;
    }

}
