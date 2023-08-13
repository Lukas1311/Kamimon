package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.EncounterMember;
import de.uniks.stpmon.k.service.storage.cache.EncounterMonsters;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Facade for the encounter session. This can be used to access the current encounter session. This should be the only
 * way to access the encounter session. This helps to mock the encounter session in tests.
 */
@Singleton
public class SessionService extends DestructibleElement {

    @Inject
    EncounterStorage encounterStorage;
    @Inject
    EventListener eventListener;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    Provider<EncounterMember> monsterCacheProvider;
    @Inject
    Provider<EncounterMonsters> monstersProvider;
    @Inject
    Provider<OpponentCache> opponentCacheProvider;
    @Inject
    EncounterService encounterService;
    @Inject
    CacheManager cacheManager;

    @Inject
    public SessionService() {
    }


    //---------------- Encounter Operations -------------------------
    public boolean hasNoEncounter() {
        return encounterStorage.isEmpty();
    }

    /**
     * Tries to load the current encounter from the server if the trainer has an associated opponent.
     *
     * @return A completable that completes when the encounter is loaded or an error occurs.
     */
    public Completable tryLoadEncounter() {
        Trainer trainer = trainerStorage.getTrainer();
        return encounterService.getTrainerOpponents(trainer._id()).flatMap((opponents) -> {
            if (opponents.isEmpty()) {
                return Observable.empty();
            }
            return Observable.just(opponents.get(0));
        }).flatMapCompletable(this::loadEncounter);
    }

    /**
     * Listens for newly created opponents of the current trainer and loads the encounter if one is created.
     *
     * @return A completable that completes when the encounter is loaded or an error occurs.
     */
    public Completable listenForEncounter() {
        return eventListener.listen(Socket.WS,
                "encounters.*.trainers.%s.opponents.*.created".formatted(trainerStorage.getTrainer()._id()),
                Opponent.class
        ).map(Event::data).take(1).flatMapCompletable(this::loadEncounter);
    }

    /**
     * Loads the encounter for the given opponent.
     * po
     *
     * @param opponent The opponent to load the encounter for.
     * @return A completable that completes when the encounter is loaded or an error occurs.
     */
    private Completable loadEncounter(Opponent opponent) {
        return encounterService.getEncounter(opponent.encounter()).map(encounter -> {
            encounterStorage.setEncounter(encounter);
            return encounter;
        }).flatMapCompletable((e) -> createSession());
    }

    /**
     * Creates a new session for the current encounter. A session is a local representation of the encounter that
     * contains all the data needed to play the encounter. Including cached monsters and opponents.
     *
     * @return A completable that completes when the session is created or an error occurs.
     */
    private Completable createSession() {
        return encounterService.getEncounterOpponents().flatMap(opponents -> {
            if (opponents.isEmpty()) {
                return Observable.empty();
            }

            Encounter encounter = encounterStorage.getEncounter();
            OpponentCache opponentCache = opponentCacheProvider.get();
            opponentCache.setup(encounter._id(), opponents);
            opponentCache.init();
            return opponentCache.onInitialized().andThen(Observable.just(opponentCache));
        }).flatMapCompletable(cache -> {
            EncounterSession session = new EncounterSession(cache);
            session.setup(monsterCacheProvider, monstersProvider,
                    trainerStorage.getTrainer()._id());
            encounterStorage.setSession(session);

            return session.waitForLoad();
        });
    }

    private <T> T applyIfEncounter(BiFunction<EncounterSession, EncounterSlot, T> getter, EncounterSlot slot) {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return getter.apply(session, slot);
    }

    //---------------- Session Helpers -------------------------
    public boolean isMonsterDead(EncounterSlot slot) {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        Opponent opponent = session.getOpponent(slot);
        if (opponent == null || opponent.monster() == null) {
            return true;
        }
        MonsterState state = session.getMonsterState(slot);
        return state == MonsterState.DEAD;
    }

    public boolean isMonsterDead(Monster monster) {
        return monster.currentAttributes().health() <= 0;
    }

    public boolean hasWon() {
        EncounterSession session = encounterStorage.getSession();
        for (EncounterSlot slot : getSlots()) {
            if (!slot.enemy()) {
                continue;
            }
            MonsterState state = session.getMonsterState(slot);
            if (state == MonsterState.ALIVE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the user has two active monsters / opponents in the encounter.
     *
     * @return True if the user has two active monsters / opponents in the encounter.
     */
    public boolean hasTwoActiveMonster() {
        if (!hasSlot(EncounterSlot.PARTY_SECOND)) {
            return false;
        }
        return getTrainer(EncounterSlot.PARTY_FIRST)
                .equals(getTrainer(EncounterSlot.PARTY_SECOND));
    }

    //---------------- Session Getters -------------------------
    public EncounterSlot getTarget(String targetId) {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        if (targetId == null) {
            throw new IllegalArgumentException("Target id must not be null");
        }
        EncounterSlot slot = session.getSlotForOpponent(targetId);
        if (slot == null) {
            slot = session.getSlotForTrainer(targetId);
        }
        if (slot == null) {
            throw new IllegalArgumentException("No slot found for target id: " + targetId);
        }
        return slot;
    }

    public Monster getMonster(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::getMonster, slot);
    }

    public Observable<Monster> listenMonster(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::listenMonster, slot);
    }

    public Observable<Opponent> listenOpponent(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::listenOpponent, slot);
    }

    public Observable<Opponent> listenOpponentDeletion(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::listenDeadOpponent, slot);
    }

    public Opponent getOpponent(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::getOpponent, slot);
    }

    public boolean isSelf(EncounterSlot slot) {
        Trainer trainer = trainerStorage.getTrainer();
        if (encounterStorage.getSession() == null) {
            return false;
        }
        return Objects.equals(trainer._id(), getTrainer(slot));
    }

    public String getTrainer(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::getTrainer, slot);
    }

    public boolean hasSlot(EncounterSlot slot) {
        return applyIfEncounter(EncounterSession::hasSlot, slot);
    }

    public Monster getMonsterById(String id) {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return null;
        }
        return session.getMonsterById(id);
    }

    public Collection<EncounterSlot> getSlots() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getSlots();
    }

    public Collection<EncounterSlot> getOwnSlots() {
        if (hasTwoActiveMonster()) {
            return List.of(EncounterSlot.PARTY_FIRST, EncounterSlot.PARTY_SECOND);
        }
        return List.of(EncounterSlot.PARTY_FIRST);
    }

    public List<String> getEnemyTeam() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getEnemyTeam();
    }

    public List<String> getOwnTeam() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getOwnTeam();
    }

    public Completable onEncounterCompleted() {
        if (encounterStorage.getEncounter() == null) {
            return Completable.complete();
        }
        return eventListener.listen(Socket.WS, "regions.*.encounters.%s.deleted"
                        .formatted(encounterStorage.getEncounter()._id()), Encounter.class)
                .map(Event::data).take(1).ignoreElements();
    }

    public void clearEncounter() {
        encounterStorage.setEncounter(null);
        encounterStorage.setSession(null);
    }
}
