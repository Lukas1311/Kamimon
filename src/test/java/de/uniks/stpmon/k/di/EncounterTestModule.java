package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Module
public class EncounterTestModule {

    @Provides
    @Singleton
    static EncounterApiService encounterApiService() {
        return new EncounterApiService() {
            final Subject<Event<Monster>> monsterEvents = PublishSubject.create();
            final Subject<Event<Opponent>> opponentEvents = PublishSubject.create();
            @Inject
            EventListener eventListener;
            String opponentId = "0";
            final EncounterWrapper encounterWrapper = initDummyEncounter(opponentId);

            private class EncounterWrapper {

                private final Encounter encounter;
                private final List<Opponent> opponentList;
                private final List<Monster> monsterList;
                final Map<String, List<Encounter>> encounterHashMap = new HashMap<>();

                public EncounterWrapper(Encounter encounter, String opponentId) {
                    this.encounter = encounter;
                    this.opponentList = initDummyOpponent(encounter, opponentId);
                    this.monsterList = initDummyMonsters();
                    this.encounterHashMap.put(encounter.region(), List.of(encounter));
                }

                private List<Opponent> initDummyOpponent(Encounter encounter, String opponentId) {
                    Opponent opponent0 = new Opponent(
                            opponentId,
                            encounter._id(),
                            "trainerId",
                            true,
                            false,
                            monsterList.get(0)._id(),
                            new AbilityMove(
                                    "ability",
                                    0,
                                    "targetId"
                            ),
                            new Result(
                                    "ability-success",
                                    0,
                                    "effective"
                            ),
                            0
                    );
                    Opponent opponent1 = new Opponent(
                            "1",
                            encounter._id(),
                            "trainer1Id",
                            false,
                            true,
                            monsterList.get(1)._id(),
                            new ChangeMonsterMove(
                                    "change-monster",
                                    "monster2Id"
                            ),
                            new Result(
                                    "monster-changed",
                                    null,
                                    null
                            ),
                            0
                    );
                    return List.of(opponent0, opponent1);
                }

                private List<Monster> initDummyMonsters() {
                    //dummy ability
                    SortedMap<String, Integer> abilities = new TreeMap<>();
                    abilities.put("Tackle", 20);

                    Monster monster1 = new Monster(
                            "0",
                            "0",
                            1,
                            1,
                            1,
                            abilities,
                            null,
                            new MonsterAttributes(
                                    20,
                                    20,
                                    20,
                                    20));
                    Monster monster2 = new Monster(
                            "1",
                            "1",
                            2,
                            2,
                            2,
                            null,
                            null,
                            new MonsterAttributes(
                                    20,
                                    20,
                                    20,
                                    20));
                    return List.of(monster1, monster2);
                }

            }

            private EncounterWrapper initDummyEncounter(String opponentId) {
                return new EncounterWrapper(new Encounter(
                        "0",
                        "0",
                        false
                ), opponentId);
            }

            @Override
            public Observable<List<Encounter>> getEncounters(String regionId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                List<Encounter> encounterList = encounterWrapper.encounterHashMap.get(regionId);
                if (encounterList != null) {
                    return Observable.just(encounterList);
                }
                return Observable.empty();
            }

            @Override
            public Observable<Encounter> getEncounter(String regionId, String id) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                List<Encounter> encounterList = encounterWrapper.encounterHashMap.get(regionId);
                if (encounterList == null || encounterList.isEmpty()) {
                    return Observable.error(new Throwable("404 Not found"));
                }
                Optional<Encounter> encounterOptional = encounterList.stream().filter(a -> a._id().equals(id)).findFirst();
                return encounterOptional.map(Observable::just).orElseGet(Observable::empty);
            }

            @Override
            public Observable<List<Opponent>> getTrainerOpponents(String regionId, String trainerId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                if (encounterWrapper.opponentList.stream().anyMatch(m -> m.trainer().equals(trainerId))) {
                    return Observable.just(encounterWrapper.opponentList.stream().filter(m -> m.trainer().equals(trainerId)).toList());
                }
                return Observable.just(new ArrayList<>());
            }

            @Override
            public Observable<List<Opponent>> getEncounterOpponents(String regionId, String encounterId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                return Observable.just(encounterWrapper.opponentList.stream().filter(m -> m.encounter().equals(encounterId)).toList());
            }

            @Override
            public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String opponentId) {
                Optional<Opponent> opponentOptional = encounterWrapper.opponentList
                        .stream().filter(m -> m._id().equals(opponentId)).findFirst();
                return opponentOptional.map(m -> Observable.just(opponentOptional.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            //unused can be removed as soon as it gets called in the critical path v3
            @SuppressWarnings("unused")
            public void mockEvents(String opponentId) {
                this.opponentId = opponentId;

                Mockito.when(eventListener.listen(Socket.WS, "encounter.%s.opponents.*.*".formatted(opponentId), Opponent.class))
                        .thenReturn(opponentEvents);

                Mockito.when(eventListener.listen(Socket.WS, "trainers.%s.monsters.*.*".formatted(opponentId), Monster.class))
                        .thenReturn(monsterEvents);
            }

            @Override
            public Observable<Opponent> makeMove(String regionId, String encounterId, String opponentId, UpdateOpponentDto opponentDto) {
                if (regionId.isEmpty() || encounterId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + " or " + encounterId + " is empty"));
                }

                // we only mock 1 v 1 encounters
                if (encounterWrapper.opponentList.get(0)._id().equals(opponentId) && encounterWrapper.opponentList.get(0).move() instanceof AbilityMove) {
                    //manipulate the uses of the first ability by replacing it with the same ability but fewer uses -> don't know if this is the right way
                    encounterWrapper.monsterList.get(0).abilities().replace("Tackle", 19);

                    Monster updatedTarget = new Monster("1",
                            "1",
                            2,
                            2,
                            2,
                            null,
                            null,
                            new MonsterAttributes(
                                    19,
                                    20,
                                    20,
                                    20));
                    monsterEvents.onNext(new Event<>("trainers.%s.monsters.%s.updated".formatted(
                            encounterWrapper.monsterList.get(0).trainer(),
                            encounterWrapper.monsterList.get(0)),
                            updatedTarget));

                    Result result = new Result(
                            "ability-success",
                            0,
                            "normal"
                    );

                    Opponent opponent = new Opponent(opponentId,
                            encounterId,
                            "0",
                            true,
                            false,
                            opponentDto.monster(),
                            opponentDto.move(),
                            result,
                            1
                    );
                    return Observable.just(opponent);
                    //this is for the server move
                } else if (encounterWrapper.opponentList.get(1)._id().equals(opponentId)) {
                    Monster updatedTarget = new Monster("0",
                            "0",
                            1,
                            1,
                            1,
                            null,
                            null,
                            new MonsterAttributes(
                                    19,
                                    20,
                                    20,
                                    20));
                    monsterEvents.onNext(new Event<>("trainers.%s.monsters.%s.updated".formatted(
                            encounterWrapper.monsterList.get(1).trainer(),
                            encounterWrapper.monsterList.get(1)),
                            updatedTarget));

                    Result result = new Result(
                            "ability-success",
                            0,
                            "normal"
                    );

                    Opponent opponent = new Opponent(encounterWrapper.opponentList.get(1)._id(),
                            encounterId,
                            encounterWrapper.opponentList.get(1).trainer(),
                            true,
                            true,
                            opponentDto.monster(),
                            opponentDto.move(),
                            result,
                            1
                    );
                    return Observable.just(opponent);
                }
                return Observable.error(new Throwable("404 Not found"));
            }

            //unused can be removed as soon as it gets called in the critical path v3
            @SuppressWarnings("unused")
            public Observable<Opponent> makeServerMove() {
                //use get(1) to get the server opponent
                Opponent opponent = makeMove(encounterWrapper.encounter.region(), encounterWrapper.encounter._id(), encounterWrapper.opponentList.get(1)._id(), null).blockingFirst();

                opponentEvents.onNext(new Event<>("encounter.%s.opponents.%s.updated".formatted(encounterWrapper.opponentList.get(1)._id(), opponent._id()), opponent));

                return Observable.just(opponent);
            }

            @Override
            public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String opponentId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                Optional<Opponent> opponentOptional = encounterWrapper.opponentList
                        .stream().filter(m -> m._id().equals(opponentId)).findFirst();
                if (opponentOptional.isPresent()) {
                    //noinspection DataFlowIssue
                    encounterWrapper.opponentList.remove(opponentOptional.get());
                    Opponent opponent = opponentOptional.get();
                    //deleted is used for fleeing
                    opponentEvents.onNext(new Event<>("encounter.%s.opponents.%s.deleted".formatted(opponentId, opponent._id()), opponent));
                    return Observable.just(opponent);
                }
                return Observable.error(new Throwable("404 Not found"));
            }
        };
    }

}
