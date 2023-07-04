package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import de.uniks.stpmon.k.service.storage.cache.SingleMonsterCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class EncounterService {

    public enum Moves {

        CHANGE_MONSTER("change-monster"),
        ABILITY("ability");

        private final String move;

        Moves(final String move) {
            this.move = move;
        }

        @Override
        public String toString() {
            return move;
        }
    }

    @Inject
    EncounterApiService encounterApiService;

    @Inject
    RegionStorage regionStorage;

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
    public EncounterService() {
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
        return getTrainerOpponents(trainer._id()).flatMap((opponents) -> {
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
        return encounterApiService.getEncounter(
                regionStorage.getRegion()._id(),
                opponent.encounter()
        ).map(encounter -> {
            encounterStorage.setEncounter(encounter);
            return encounter;
        }).flatMapCompletable((e) -> createSession());
    }

    /**
     * Creates a new session for the current encounter. A session is a local representation of the encounter that
     * contains all the data needed to play the encounter. Including cached monsters and opponents.
     *
     * @return
     */
    private Completable createSession() {
        return getEncounterOpponents().flatMap(opponents -> {
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
            encounterStorage.setEncounterSession(session);

            return session.waitForLoad();
        });
    }


    //---------------- Region Encounters ----------------------------

    public Observable<List<Encounter>> getEncounters() {
        return encounterApiService.getEncounters(regionStorage.getRegion()._id());
    }

    public Observable<Encounter> getEncounter() {
        return encounterApiService.getEncounter(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id()
        );
    }

    //---------------- Encounters Opponents ----------------------------

    public Observable<List<Opponent>> getTrainerOpponents(String trainerId) {
        return encounterApiService.getTrainerOpponents(regionStorage.getRegion()._id(), trainerId);
    }

    public Observable<List<Opponent>> getEncounterOpponents() {
        return encounterApiService.getEncounterOpponents(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id()
        );
    }

    public Observable<Opponent> getEncounterOpponent() {
        return encounterApiService.getEncounterOpponent(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id()
        );
    }

    public Observable<Opponent> makeAbilityMove(Monster attacker, int ability, Monster target) {
        UpdateOpponentDto dto = new UpdateOpponentDto(attacker._id(), new AbilityMove(
                Moves.ABILITY.toString(),
                ability,
                target._id())
        );
        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id(),
                dto
        );
    }

    public Observable<Opponent> makeChangeMonsterMove(Monster currentMonster, Monster nextMonster) {
        UpdateOpponentDto dto = new UpdateOpponentDto(currentMonster._id(), new ChangeMonsterMove(
                Moves.CHANGE_MONSTER.toString(),
                nextMonster._id())
        );
        return encounterApiService.makeMove(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id(),
                dto
        );
    }

    public Observable<Opponent> fleeEncounter() {
        return encounterApiService.fleeEncounter(
                regionStorage.getRegion()._id(),
                encounterStorage.getEncounter()._id(),
                encounterStorage.getOpponentList().get(0)._id()
        );
    }

}
