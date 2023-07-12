package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@SuppressWarnings("unused")
@Singleton
public class EncounterApiDummy implements EncounterApiService {
    @Inject
    EventDummy eventDummy;
    final String opponentId = "0";
    EncounterWrapper encounterWrapper;

    @Inject
    public EncounterApiDummy() {
    }

    @SuppressWarnings("SameParameterValue")
    private EncounterWrapper initDummyEncounter(String opponentId, boolean big) {
        return new EncounterWrapper(new Encounter(
                "0",
                "id0",
                false
        ), opponentId, big);
    }

    public void startEncounter() {
        startEncounter(false);
    }

    public void startEncounter(boolean big) {
        encounterWrapper = initDummyEncounter(opponentId, big);
        for (Opponent opponent : encounterWrapper.opponentList) {
            sendOpponentEvent(opponent, "created");
        }
    }

    @Override
    public Observable<List<Encounter>> getEncounters(String regionId) {
        if (encounterWrapper == null) {
            return Observable.empty();
        }
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
        if (encounterWrapper == null) {
            return Observable.empty();
        }
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        List<Encounter> encounterList = encounterWrapper.encounterHashMap.get(regionId);
        if (encounterList == null || encounterList.isEmpty()) {
            return Observable.error(new Throwable(id + " does not exist"));
        }
        Optional<Encounter> encounterOptional = encounterList.stream().filter(a -> a._id().equals(id)).findFirst();
        return encounterOptional.map(Observable::just).orElseGet(Observable::empty);
    }

    @Override
    public Observable<List<Opponent>> getTrainerOpponents(String regionId, String trainerId) {
        if (encounterWrapper == null) {
            return Observable.empty();
        }
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        return Observable.just(encounterWrapper.opponentList
                .stream().filter(m -> m.trainer().equals(trainerId))
                .toList());
    }

    @Override
    public Observable<List<Opponent>> getEncounterOpponents(String regionId, String encounterId) {
        if (encounterWrapper == null) {
            return Observable.empty();
        }
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        return Observable.just(encounterWrapper.opponentList
                .stream().filter(m -> m.encounter().equals(encounterId))
                .toList());
    }

    @Override
    public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String opponentId) {
        if (encounterWrapper == null) {
            return Observable.empty();
        }
        Optional<Opponent> opponentOptional = encounterWrapper.opponentList
                .stream().filter(m -> m._id().equals(opponentId)).findFirst();
        return opponentOptional.map(m -> Observable.just(opponentOptional.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }

    @Override
    public Observable<Opponent> makeMove(String regionId, String encounterId, String opponentId, UpdateOpponentDto opponentDto) {
        if (encounterWrapper == null) {
            throw new IllegalStateException("Encounter not started");
        }
        if (regionId.isEmpty() || encounterId.isEmpty()) {
            return Observable.error(new Throwable(regionId + " or " + encounterId + " is empty"));
        }

        // we only mock 1 v 1 encounters
        Opponent playerOpponent = encounterWrapper.opponentList.get(0);
        Opponent enemyOpponent = encounterWrapper.opponentList.get(1);
        if (playerOpponent._id().equals(opponentId) && playerOpponent.move() instanceof AbilityMove) {
            //manipulate the uses of the first ability by replacing it with the same ability but fewer uses -> don't know if this is the right way
            encounterWrapper.monsterList.get(0).abilities().replace("Tackle", 19);

            Monster updatedTarget = new Monster("1",
                    "attacker",
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
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.updated".formatted(
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
                    List.of(result),
                    1
            );
            sendOpponentEvent(opponent, "updated");
            return Observable.just(opponent);
            //this is for the server move
        } else if (enemyOpponent._id().equals(opponentId)) {
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
            eventDummy.sendEvent(new Event<>("trainers.%s.monsters.%s.updated".formatted(
                    encounterWrapper.monsterList.get(1).trainer(),
                    encounterWrapper.monsterList.get(1)),
                    updatedTarget));

            Result result = new Result(
                    "ability-success",
                    0,
                    "normal"
            );

            Opponent opponent = new Opponent(opponentId,
                    encounterId,
                    enemyOpponent.trainer(),
                    true,
                    true,
                    opponentDto.monster(),
                    opponentDto.move(),
                    List.of(result),
                    1
            );

            sendOpponentEvent(opponent, "updated");
            return Observable.just(opponent);
        }

        return Observable.error(new Throwable("404 Not found"));
    }

    //unused can be removed as soon as it gets called in the critical path v3
    @SuppressWarnings("unused")
    public Observable<Opponent> makeServerMove() {
        if (encounterWrapper == null) {
            throw new IllegalStateException("Encounter not started");
        }
        //use get(1) to get the server opponent
        Opponent opponent = makeMove(encounterWrapper.encounter.region(), encounterWrapper.encounter._id(),
                encounterWrapper.opponentList.get(1)._id(), null).blockingFirst();

        sendOpponentEvent(opponent, "updated");

        return Observable.just(opponent);
    }

    private void sendOpponentEvent(Opponent opponent, String event) {
        eventDummy.sendEvent(new Event<>("encounters.%s.trainers.%s.opponents.%s.%s"
                .formatted(opponent.encounter(), opponent.trainer(), opponent._id(), event), opponent));
    }

    @Override
    public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String opponentId) {
        if (encounterWrapper == null) {
            throw new IllegalStateException("Encounter not started");
        }
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
            sendOpponentEvent(opponent, "deleted");
            return Observable.just(opponent);
        }
        return Observable.error(new Throwable("404 Not found"));
    }

    private static class EncounterWrapper {
        private final Encounter encounter;
        private final List<Opponent> opponentList;
        private final List<Monster> monsterList;
        final Map<String, List<Encounter>> encounterHashMap = new HashMap<>();

        public EncounterWrapper(Encounter encounter, String opponentId, boolean big) {
            this.encounter = encounter;
            this.monsterList = initDummyMonsters();
            this.opponentList = initDummyOpponent(encounter, opponentId, big);
            this.encounterHashMap.put(encounter.region(), List.of(encounter));
        }

        private List<Opponent> initDummyOpponent(Encounter encounter, String opponentId, boolean big) {
            Opponent opponent0 = new Opponent(
                    opponentId,
                    encounter._id(),
                    "0",
                    true,
                    false,
                    monsterList.get(0)._id(),
                    new AbilityMove(
                            "ability",
                            0,
                            "targetId"
                    ),
                    List.of(new Result(
                            "ability-success",
                            0,
                            "effective"
                    )),
                    0
            );
            Opponent opponent1 = new Opponent(
                    "1",
                    encounter._id(),
                    "attacker",
                    false,
                    true,
                    monsterList.get(1)._id(),
                    new ChangeMonsterMove(
                            "change-monster",
                            "monster2Id"
                    ),
                    List.of(new Result(
                            "monster-changed",
                            null,
                            null
                    )),
                    0
            );
            if (!big) {
                return List.of(opponent0, opponent1);
            }
            Opponent opponent2 = new Opponent(
                    "2",
                    encounter._id(),
                    "attacker1",
                    false,
                    true,
                    monsterList.get(1)._id(),
                    new ChangeMonsterMove(
                            "change-monster",
                            "monster2Id"
                    ),
                    List.of(new Result(
                            "monster-changed",
                            null,
                            null
                    )),
                    0
            );
            Opponent opponent3 = new Opponent(
                    "3",
                    encounter._id(),
                    "defender1",
                    true,
                    true,
                    monsterList.get(1)._id(),
                    new ChangeMonsterMove(
                            "change-monster",
                            "monster2Id"
                    ),
                    List.of(new Result(
                            "monster-changed",
                            null,
                            null
                    )),
                    0
            );
            return List.of(opponent0, opponent1, opponent2, opponent3);
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
                    "attacker",
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
}
