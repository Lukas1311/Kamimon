package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MonsterService extends DestructibleElement {

    private boolean isInitialized = false;
    @Inject
    CacheManager cacheManager;
    @Inject
    TrainerStorage trainerStorage;

    private MonsterCache monsterCache;

    @Inject
    public MonsterService() {

    }

    private void init() {
        if (isInitialized) {
            return;
        }
        // listen to trainer changes and update monster cache
        onDestroy(
                trainerStorage.onTrainer().subscribe(
                        trainer -> monsterCache = trainer
                                .map(value -> cacheManager.requestMonsters(value._id()))
                                .orElse(null)
                )
        );
        isInitialized = true;
    }

    public Observable<List<Monster>> getTeam() {
        init();
        if (monsterCache == null) {
            return Observable.empty();
        }
        return getTeamCache().getValues();
    }

    public Observable<Optional<Monster>> getMonster(String id) {
        init();
        if (monsterCache == null) {
            return Observable.empty();
        }
        return getMonsterCache().listenValue(id);
    }

    public Observable<List<Monster>> getMonsters() {
        init();
        if (monsterCache == null) {
            return Observable.empty();
        }
        return getMonsterCache().getValues();
    }

    public boolean isCatched(Integer type) {
        init();
        if (monsterCache == null) {
            return false;
        }
        return getMonsterCache()
                .getValues()
                .blockingFirst()
                .stream()
                .anyMatch(mon -> Objects.equals(mon.type(), type));
    }

    public ICache<Monster, String> getTeamCache() {
        return getMonsterCache().getTeam();
    }

    public MonsterCache getMonsterCache() {
        init();
        if (monsterCache == null) {
            throw new IllegalStateException("MonsterCache is not initialized, probably trainer not set");
        }
        return monsterCache;
    }

}
