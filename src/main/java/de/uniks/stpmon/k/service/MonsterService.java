package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class MonsterService extends DestructibleElement {

    private boolean isInitialized = false;
    @Inject
    CacheManager cacheManager;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;

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

    /**
     * Determines if any monster is still alive
     *
     * @param trainerId the id of the trainer
     * @return observable that emits true if any monster is alive, false otherwise
     */
    public Observable<Boolean> anyMonsterAlive(String trainerId) {
        String regionId = regionStorage.getRegion()._id();
        return regionService.getMonsters(regionId, trainerId)
                .map(this::anyMonsterAlive);
    }

    /**
     * Determines if any monster is still alive
     *
     * @return true if any monster is alive, false otherwise
     */
    public boolean anyMonsterAlive() {
        init();
        if (monsterCache == null) {
            return false;
        }
        return anyMonsterAlive(monsterCache.getTeam().getCurrentValues());
    }

    private boolean anyMonsterAlive(List<Monster> monsters) {
        return monsters.stream()
                .anyMatch(monster -> monster.currentAttributes().health() > 0);
    }

    /**
     * Determines if any monster is damaged.
     *
     * @return true if any monster is damaged, false otherwise
     */
    public boolean anyMonsterDamaged() {
        init();
        if (monsterCache == null) {
            return true;
        }
        return monsterCache.getCurrentValues().stream()
                .anyMatch(monster -> monster.currentAttributes().health() < monster.attributes().health());
    }

    public List<Monster> getTeamList() {
        init();
        if (monsterCache == null) {
            return List.of();
        }
        return getTeamCache().getCurrentValues();
    }

    public List<Monster> getMonsterList() {
        init();
        if (monsterCache == null) {
            return List.of();
        }
        return getMonsterCache().getCurrentValues();
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
