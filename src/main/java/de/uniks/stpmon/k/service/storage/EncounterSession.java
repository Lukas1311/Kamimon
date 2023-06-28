package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.DestructibleElement;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.MonsterCache;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import io.reactivex.rxjava3.core.Observable;

import java.util.*;

public class EncounterSession extends DestructibleElement {

    private final OpponentCache opponentCache;
    private final String self;
    private final String teammate;
    private final List<String> opposingTeam;
    private final Map<String, MonsterCache> monsterCaches = new HashMap<>();

    public EncounterSession(OpponentCache opponentCache, CacheManager cacheManager, String self) {
        this.opponentCache = opponentCache;
        this.self = self;
        this.opposingTeam = new LinkedList<>();
        String teammate = null;
        // does not block because it is initialized with the initial values
        for (Opponent op : opponentCache.getValues().blockingFirst()) {
            monsterCaches.put(op._id(), cacheManager.requestMonsters(op._id()));
            if (op._id().equals(self)) {
                continue;
            }
            if (!op.isAttacker()) {
                teammate = op._id();
            } else {
                opposingTeam.add(op._id());
            }
        }
        this.teammate = teammate;
        onDestroy(opponentCache::destroy);
    }

    public Observable<Opponent> getSelf() {
        return opponentCache.listenValue(self).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public MonsterCache getMonsters(String id) {
        return monsterCaches.get(id);
    }

    public Observable<Opponent> getTeammate() {
        return opponentCache.listenValue(teammate).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public int getEnemies() {
        return opposingTeam.size();
    }

    public Observable<Opponent> getFirstEnemy() {
        if (getEnemies() < 0) {
            return Observable.error(new IllegalStateException("No enemies found!"));
        }
        return opponentCache.listenValue(opposingTeam.get(0)).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<Opponent> getSecondEnemy() {
        if (getEnemies() < 1) {
            return Observable.error(new IllegalStateException("Second enemy not found!"));
        }
        return opponentCache.listenValue(opposingTeam.get(1)).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public List<String> getOpposingTeam() {
        return Collections.unmodifiableList(opposingTeam);
    }

    public Set<String> getOwnTeam() {
        return Set.of(self, teammate);
    }

    public OpponentCache getOpponentCache() {
        return opponentCache;
    }
}
