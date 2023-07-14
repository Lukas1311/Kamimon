package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
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
    RegionApiDummy regionApi;
    @Inject
    PresetApiDummy presetApiDummy;

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
        if (checkState()) {
            return Observable.empty();
        }

        // we only mock 1 v 1 encounters
        Opponent playerOpponent = encounterWrapper.opponentList.get(0);
        Opponent enemyOpponent = encounterWrapper.opponentList.get(1);
        if (playerOpponent._id().equals(opponentId) && opponentDto.move() instanceof AbilityMove abilityMove) {
            //manipulate the uses of the first ability by replacing it with the same ability but fewer uses -> don't know if this is the right way
//            encounterWrapper.monsterList.get(0).abilities().replace("Tackle", 19);
//            Monster currentTarget =
//                    regionApi.getMonster(RegionApiDummy.REGION_ID, enemyOpponent.trainer(), enemyOpponent.monster())
//                            .blockingFirst();
//            Monster updatedTarget = MonsterBuilder.builder(currentTarget)
//                    .setCurrentAttributes(new MonsterAttributes(1f, 0f, 0f, 0f))
//                    .create();
//            regionApi.updateMonster(updatedTarget);
//
//            Result result = new Result(
//                    "ability-success",
//                    0,
//                    "normal"
//            );
//            Opponent moveOp = OpponentBuilder.builder(enemyOpponent)
//                    .setMove(opponentDto.move())
//                    .create();
//            sendOpponentEvent(moveOp, "updated");
//
//            Opponent opponent = OpponentBuilder.builder(enemyOpponent)
//                    .setResults(List.of(result))
//                    .create();
//            sendOpponentEvent(opponent, "updated");
            Opponent opponent = attack(enemyOpponent, abilityMove, opponentDto);
            checkState();
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
                            19f,
                            20f,
                            20f,
                            20f));
            regionApi.updateMonster(updatedTarget);

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
            checkState();
            return Observable.just(opponent);
        }

        return Observable.error(new Throwable("404 Not found"));
    }

    private Opponent attack(Opponent target, AbilityMove move, UpdateOpponentDto opponentDto) {
        AbilityDto abilityDto = presetApiDummy.abilities.get(move.ability());
        //manipulate the uses of the first ability by replacing it with the same ability but fewer uses -> don't know if this is the right way
        // encounterWrapper.monsterList.get(abilityDto.id()).abilities().replace("Tackle", 19);
        Monster currentTarget =
                regionApi.getMonster(RegionApiDummy.REGION_ID, target.trainer(), target.monster())
                        .blockingFirst();
        MonsterAttributes current = currentTarget.currentAttributes();
        Monster updatedTarget = MonsterBuilder.builder(currentTarget)
                .setCurrentAttributes(new MonsterAttributes(current.health() - abilityDto.power(), 0f, 0f, 0f))
                .create();
        regionApi.updateMonster(updatedTarget);

        Result result = new Result(
                "ability-success",
                0,
                "normal"
        );
        Opponent moveOp = OpponentBuilder.builder(target)
                .setMove(opponentDto.move())
                .create();
        sendOpponentEvent(moveOp, "updated");

        Opponent opponent = OpponentBuilder.builder(target)
                .setResults(List.of(result))
                .create();
        sendOpponentEvent(opponent, "updated");
        return opponent;
    }

    private boolean checkState() {
        if (encounterWrapper == null) {
            throw new IllegalStateException("Encounter not started");
        }
        boolean attackerAlive = false;
        boolean defenderAlive = false;
        for (Opponent opponent : encounterWrapper.opponentList) {
            if (opponent.monster() == null) {
                continue;
            }
            Monster monster = regionApi.getMonster(RegionApiDummy.REGION_ID, opponent.trainer(), opponent.monster())
                    .blockingFirst();
            if (monster.currentAttributes().health() > 0) {
                if (opponent.isAttacker()) {
                    attackerAlive = true;
                } else {
                    defenderAlive = true;
                }
            }
        }
        if (attackerAlive && defenderAlive) {
            return false;
        }
        eventDummy.sendEvent(new Event<>("regions.%s.encounters.%s.deleted".formatted(
                RegionApiDummy.REGION_ID,
                encounterWrapper.encounter._id()),
                encounterWrapper.encounter));
        for (Opponent opponent : encounterWrapper.opponentList) {
            sendOpponentEvent(opponent, "deleted");
        }
        encounterWrapper = null;
        return true;
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
                    "0",
                    null,
                    List.of(),
                    0
            );
            Opponent opponent1 = new Opponent(
                    "1",
                    encounter._id(),
                    "attacker",
                    false,
                    true,
                    "1",
                    null,
                    List.of(),
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
                    "2",
                    null,
                    List.of(),
                    0
            );
            Opponent opponent3 = new Opponent(
                    "3",
                    encounter._id(),
                    "defender1",
                    true,
                    true,
                    "3",
                    null,
                    List.of(),
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
                            20f,
                            20f,
                            20f,
                            20f));
            Monster monster2 = new Monster(
                    "1",
                    "attacker",
                    2,
                    2,
                    2,
                    null,
                    null,
                    new MonsterAttributes(
                            20f,
                            20f,
                            20f,
                            20f));
            return List.of(monster1, monster2);
        }

    }
}
