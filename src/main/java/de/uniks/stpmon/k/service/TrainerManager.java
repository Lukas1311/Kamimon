package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.service.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.TrainerProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TrainerManager implements ILifecycleService {
    private final Map<String, TrainerProvider> trainers = new HashMap<>();
    private final Map<String, MonsterCache> monsters = new HashMap<>();

    @Inject
    protected Provider<MonsterCache> cacheProvider;

    @Inject
    public TrainerManager() {
    }

    public TrainerProvider requestStorage(String trainerId) {
        TrainerProvider provider = trainers.get(trainerId);
        if (provider == null) {
            provider = new TrainerProvider();
            trainers.put(trainerId, provider);
        }
        return provider;
    }

    public MonsterCache requestMonsters(String trainerId) {
        MonsterCache provider = monsters.get(trainerId);
        if (provider == null) {
            provider = cacheProvider.get();
            provider.setTrainerId(trainerId);
            provider.init(() -> monsters.remove(trainerId));
            monsters.put(trainerId, provider);
        }
        return provider;
    }

    public void destroy() {
        for (MonsterCache cache : monsters.values()) {
            cache.destroy();
        }
        monsters.clear();
    }
}
