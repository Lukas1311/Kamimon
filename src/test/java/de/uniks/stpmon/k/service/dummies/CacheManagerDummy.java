package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;

import javax.inject.Provider;

public class CacheManagerDummy extends CacheManager {

    public static void init(CacheManager cacheManager) {
        init(cacheManager, FriendCacheDummy::new);
    }

    public static void init(CacheManager cacheManager, Provider<IFriendCache> friendProvider) {
        CacheManagerDummy dummy = (CacheManagerDummy) cacheManager;
        dummy.setFriendCacheProvider(friendProvider);
    }

    private void setFriendCacheProvider(Provider<IFriendCache> friendCacheProvider) {
        this.friendCacheProvider = friendCacheProvider;
    }

    private void setMonsterCacheProvider(Provider<MonsterCache> monsterCacheProvider) {
        this.monsterCacheProvider = monsterCacheProvider;
    }
}
