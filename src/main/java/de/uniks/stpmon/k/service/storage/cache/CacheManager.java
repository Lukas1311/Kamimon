package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.storage.TrainerProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * CacheManager is a singleton class that manages the cache of the application.
 * It can be used to acquire a caches and storage for other users or trainers.
 */
@Singleton
public class CacheManager implements ILifecycleService {
    private final Map<String, TrainerProvider> trainers = new HashMap<>();
    private final Map<String, MonsterCache> monsters = new HashMap<>();
    private IFriendCache friendCache;

    @Inject
    protected Provider<MonsterCache> monsterCacheProvider;
    @Inject
    protected Provider<IFriendCache> friendCacheProvider;
    @Inject
    protected AbilityCache abilityCache;
    @Inject
    protected MonsterTypeCache monsterTypeCache;

    @Inject
    public CacheManager() {
    }

    /**
     * Acquires a TrainerProvider for the given trainerId. If the provider does not exist yet, it will be created.
     *
     * @param trainerId The id of the trainer
     * @return The TrainerProvider for the given id.
     */
    public TrainerProvider requestTrainer(String trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("trainerId must not be null");
        }
        TrainerProvider provider = trainers.get(trainerId);
        if (provider == null) {
            provider = new TrainerProvider();
            trainers.put(trainerId, provider);
        }
        return provider;
    }

    /**
     * Acquires the FriendCache. If the cache does not exist yet, it will be created.
     *
     * @param trainerId The id of the trainer
     * @return True if the cache exists, false otherwise
     */
    public boolean hasTrainer(String trainerId) {
        return trainers.containsKey(trainerId);
    }

    /**
     * Acquires a MonsterCache for the given trainerId. If the cache does not exist yet, it will be created.
     * <p>
     * The cache can be destroyed by calling the destroy method.
     * This will also remove the cache from the manager.
     *
     * @param trainerId The id of the trainer
     * @return The MonsterCache for the given id
     */
    public MonsterCache requestMonsters(String trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("trainerId must not be null");
        }
        MonsterCache provider = monsters.get(trainerId);
        if (provider == null) {
            provider = monsterCacheProvider.get();
            provider.setTrainerId(trainerId);
            provider.addOnDestroy(() -> monsters.remove(trainerId));
            provider.init();
            monsters.put(trainerId, provider);
        }
        return provider;
    }

    /**
     * Checks if a MonsterCache exists for the given trainerId.
     *
     * @param trainerId The id of the trainer
     * @return True if the cache exists, false otherwise
     */
    public boolean hasMonsters(String trainerId) {
        return monsters.containsKey(trainerId);
    }

    /**
     * Acquires the FriendCache for the given userId. If the cache does not exist yet, it will be created.
     * <p>
     * The cache can be destroyed by calling the destroy method.
     * This will also remove the cache from the manager.
     *
     * @param userId The user that is logged in and whose friends are displayed
     * @return The friend cache for the given id.
     */
    public IFriendCache requestFriends(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null when friend cache is not present");
        }
        if (friendCache == null) {
            friendCache = friendCacheProvider.get();
            friendCache.setMainUser(userId);
            friendCache.addOnDestroy(() -> friendCache = null);
            friendCache.init();
        }
        return friendCache;
    }

    /**
     * Checks if a FriendCache exists and is initialized for the given userId.
     *
     * @return True if the cache exists, false otherwise
     */
    public boolean hasFriends(String userId) {
        return friendCache != null && friendCache.isMainUser(userId);
    }

    /**
     * Acquires the ability cache.
     * If the cache does not exist yet, it will be created.
     *
     * @return A cache for abilities
     */
    public AbilityCache abilityCache() {
        if (abilityCache.getStatus() == ICache.Status.UNINITIALIZED) {
            abilityCache.init();
        }
        return abilityCache;
    }

    /**
     * Acquires the monster type cache.
     * If the cache does not exist yet, it will be created.
     *
     * @return A cache for monster types
     */
    public MonsterTypeCache monsterTypeCache() {
        if (monsterTypeCache.getStatus() == ICache.Status.UNINITIALIZED) {
            monsterTypeCache.init();
        }
        return monsterTypeCache;
    }

    @Override
    public void destroy() {
        for (MonsterCache cache : monsters.values()) {
            cache.destroy();
        }
        trainers.clear();
        monsters.clear();
        if (friendCache != null) {
            friendCache.destroy();
        }
        if (abilityCache != null) {
            abilityCache.destroy();
        }
        if (monsterTypeCache != null) {
            monsterTypeCache.destroy();
        }
    }
}
