package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import de.uniks.stpmon.k.service.storage.cache.SingleMonsterCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    Provider<SingleMonsterCache> monsterCacheProvider;
    @Inject
    Provider<OpponentCache> opponentCacheProvider;
    @Inject
    EncounterService encounterService;

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
        ).map(Event::data).flatMapCompletable(this::loadEncounter);
    }

    /**
     * Loads the encounter for the given opponent.
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
            session.setup(monsterCacheProvider, trainerStorage.getTrainer()._id());
            encounterStorage.setSession(session);

            return session.waitForLoad();
        });
    }

    private <T> T applyIfEncounter(BiFunction<EncounterSession, EncounterMember, T> getter, EncounterMember member) {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return getter.apply(session, member);
    }


    //---------------- Session Getters -------------------------

    public Monster getMonster(EncounterMember member) {
        return applyIfEncounter(EncounterSession::getMonster, member);
    }

    public Observable<Monster> listenMonster(EncounterMember member) {
        return applyIfEncounter(EncounterSession::listenMonster, member);
    }

    public Observable<Opponent> listenOpponent(EncounterMember member) {
        return applyIfEncounter(EncounterSession::listenOpponent, member);
    }

    public Opponent getOpponent(EncounterMember member) {
        return applyIfEncounter(EncounterSession::getOpponent, member);
    }

    public boolean hasMember(EncounterMember member) {
        return applyIfEncounter(EncounterSession::hasMember, member);
    }

    public Collection<EncounterMember> getMembers() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.getMembers();
    }


    public List<String> getAttackerTeam() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getAttackerTeam();
    }

    public List<String> getOwnTeam() {
        EncounterSession session = encounterStorage.getSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getOwnTeam();
    }

}
