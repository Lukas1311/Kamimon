package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import io.reactivex.rxjava3.core.Completable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class SessionService extends DestructibleElement {

    @Inject
    TrainerStorage trainerStorage;
    @Inject
    EncounterStorage encounterStorage;
    @Inject
    EncounterService encounterService;
    @Inject
    CacheManager cacheManager;
    @Inject
    Provider<OpponentCache> opponentCacheProvider;

    @Inject
    public SessionService() {
    }

    public Completable loadEncounter() {
        return encounterService.getEncounterOpponents().map(opponents -> {
            if (opponents.isEmpty()) {
                return opponents;
            }

            Encounter encounter = encounterStorage.getEncounter();
            OpponentCache opponentCache = opponentCacheProvider.get();
            opponentCache.setup(encounter._id(), opponents);
            opponentCache.init();
            EncounterSession session = new EncounterSession(opponentCache,
                    cacheManager,
                    trainerStorage.getTrainer()._id()
            );
            encounterStorage.setEncounterSession(session);
            return opponents;
        }).ignoreElements();
    }

//    public Observable<List<Monster>> getMainMonster(String monsterId) {
//        return encounterStorage.getEncounterSession().getMonster(monsterId);
//    }
//    public Observable<List<Monster>> getAttackerMonster(String monsterId) {
//        return encounterStorage.getEncounterSession().getMonster(monsterId);
//    }

}
