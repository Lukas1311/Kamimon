package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.OpponentBuilder;
import de.uniks.stpmon.k.models.builder.ResultBuilder;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.util.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@SuppressWarnings("unused")
@Singleton
public class EncounterApiDummy implements EncounterApiService {
    @Inject
    EventDummy eventDummy;
    static final String OPPONENT_ID = "0";
    EncounterWrapper encounterWrapper;
    @Inject
    RegionApiDummy regionApi;
    @Inject
    PresetApiDummy presetApiDummy;
    final Deque<Pair<String, UpdateOpponentDto>> moveQueue = new LinkedList<>();
    private boolean evolves = false;

    @Inject
    public EncounterApiDummy() {
    }

    @SuppressWarnings("SameParameterValue")
    private EncounterWrapper initDummyEncounter(String opponentId, boolean big, boolean wild) {
        return new EncounterWrapper(new Encounter(
                "0",
                "id0",
                wild
        ), opponentId, big);
    }

    public void startEncounter() {
        startEncounter(false, false);
    }

    public void startEncounter(boolean big, boolean wild) {
        encounterWrapper = initDummyEncounter(OPPONENT_ID, big, wild);
        for (Opponent opponent : encounterWrapper.opponentByTrainer.values()) {
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
        return Observable.just(encounterWrapper.opponentByTrainer.values()
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
        return Observable.just(encounterWrapper.opponentByTrainer.values()
                .stream().filter(m -> m.encounter().equals(encounterId))
                .toList());
    }

    @Override
    public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String opponentId) {
        if (encounterWrapper == null) {
            return Observable.empty();
        }
        Optional<Opponent> opponentOptional = encounterWrapper.opponentByTrainer.values()
                .stream().filter(m -> m._id().equals(opponentId)).findFirst();
        return opponentOptional.map(m -> Observable.just(opponentOptional.get())).orElseGet(()
                -> Observable.error(new Throwable("404 Not found")));
    }


    /**
     * Adds a move for a trainer opponent to the queue to be executed if the user moves himself
     *
     * @param move the move to be added
     */
    public void addMove(String opponentId, UpdateOpponentDto move) {
        Opponent opponent = encounterWrapper.getOpponent(opponentId);
        if (opponent.isAttacker()) {
            moveQueue.addFirst(new Pair<>(opponentId, move));
        } else {
            moveQueue.addLast(new Pair<>(opponentId, move));
        }
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    private void doServerMoves(String regionId, String encounterId) {
        for (Pair<String, UpdateOpponentDto> move : moveQueue) {
            makeMove(regionId, encounterId, move.getKey(), move.getValue());
            if (checkState()) {
                return;
            }
        }
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
        boolean isPlayer = opponentId.equals("0");

        // we only mock 1 v 1 encounters
        Opponent opponent = encounterWrapper.getOpponent(opponentId);

        if (isPlayer && !opponent.isAttacker()) {
            doServerMoves(regionId, encounterId);
        }
        Opponent updated = null;
        if (opponentDto.move() instanceof AbilityMove abilityMove) {
            Opponent enemyOpponent = encounterWrapper.getOpponent(abilityMove.target());
            attack(enemyOpponent, abilityMove, opponentDto);
            updated = opponent;
            //this is for the server move
        } else if (opponentDto.move() instanceof ChangeMonsterMove abilityMove) {
            updated = switchMonster(opponent, abilityMove, opponentDto);
            //this is for the server move
        } else if (opponentDto.move() instanceof UseItemMove useItemMove) {
            catchMon(useItemMove);
            updated = opponent;
        } else if (opponentDto.monster() != null) {
            return Observable.just(forceMonster(opponent, opponentDto));
        }
        if (updated != null) {
            if (checkState()) {
                return Observable.just(updated);
            }
            if (isPlayer && opponent.isAttacker()) {
                doServerMoves(regionId, encounterId);
            }
            return Observable.just(updated);
        }

        return Observable.error(new Throwable("404 Not found"));
    }

    private Opponent forceMonster(Opponent opponent, UpdateOpponentDto opponentDto) {
        Opponent opp = OpponentBuilder.builder(opponent)
                .setMonster(opponentDto.monster())
                .create();
        sendOpponentEvent(opp, "updated");
        return opp;
    }

    private Opponent switchMonster(Opponent opponent, ChangeMonsterMove move, UpdateOpponentDto opponentDto) {
        Opponent moveOp = OpponentBuilder.builder(opponent)
                .setMove(opponentDto.move())
                .create();
        sendOpponentEvent(moveOp, "updated");

        Opponent opp = OpponentBuilder.builder(opponent)
                .setMonster(move.monster())
                .create();
        sendOpponentEvent(opp, "updated");
        return opp;
    }

    private void catchMon(UseItemMove useItemMove) {
        int itemId = useItemMove.item();
        String targetId = useItemMove.target();
        List<Result> results = new ArrayList<>();
        Result itemSucces = ResultBuilder.builder()
                .setType("item-success")
                .setItem(itemId).create();
        results.add(itemSucces);
        Result monsterCaught = ResultBuilder.builder()
                .setType("monster-caught").create();
        results.add(monsterCaught);

        Opponent me = encounterWrapper.getOpponent("0");

        Opponent moveOp = OpponentBuilder.builder(me).setMove(useItemMove).create();

        Opponent opponent = OpponentBuilder.builder(me)
                .setResults(results)
                .create();
        Platform.runLater(() -> {
            sendOpponentEvent(moveOp, "updated");
            sendOpponentEvent(opponent, "updated");
            deleteEncounter();
        });

    }

    private void attack(Opponent target, AbilityMove move, UpdateOpponentDto opponentDto) {
        AbilityDto abilityDto = presetApiDummy.abilities.get(move.ability());

        Monster currentTarget =
                regionApi.getMonster(RegionApiDummy.REGION_ID, target.trainer(), target.monster())
                        .blockingFirst();
        MonsterAttributes current = currentTarget.currentAttributes();
        Monster updatedTarget = MonsterBuilder.builder(currentTarget)
                .setCurrentAttributes(new MonsterAttributes(Math.max(current.health() - abilityDto.power(), 0)
                        , 0f, 0f, 0f))
                .create();
        regionApi.updateMonster(updatedTarget);

        List<Result> results = new ArrayList<>();

        Result result = ResultBuilder.builder("ability-success")
                .setAbility(0)
                .setEffectiveness("effective")
                .create();
        results.add(result);

        if (evolves) {

            List<Result> lvlUpResults = new ArrayList<>();
            Result lvlUpResult = ResultBuilder.builder("monster-levelup").create();
            lvlUpResults.add(lvlUpResult);
            Result evoResult = ResultBuilder.builder("monster-evolved").create();
            lvlUpResults.add(evoResult);
            Result attackLearnedResult = ResultBuilder.builder("monster-learned")
                    .setAbility(0)
                    .create();
            lvlUpResults.add(attackLearnedResult);
            Result attackForgotResult = ResultBuilder.builder("monster-forgot")
                    .setAbility(0)
                    .create();
            lvlUpResults.add(attackForgotResult);

            Opponent evo = encounterWrapper.getOpponent("0");

            //set monster before level up
            Opponent monOpp = OpponentBuilder.builder(evo)
                    .setMonster("0")
                    .create();
            sendOpponentEvent(monOpp, "updated");

            Monster currentMon =
                    regionApi.getMonster(RegionApiDummy.REGION_ID, evo.trainer(), evo.monster())
                            .blockingFirst();
            MonsterAttributes currentAtt = currentMon.currentAttributes();
            Monster updatedMon = MonsterBuilder.builder(currentMon)
                    .setLevel(currentMon.level() + 1)
                    .create();
            regionApi.updateMonster(updatedMon);

            Opponent evoOpponent = OpponentBuilder.builder(evo)
                    .setMonster("0")
                    .setResults(lvlUpResults)
                    .create();
            Platform.runLater(() ->
                    sendOpponentEvent(evoOpponent, "updated")
            );

        }

        Opponent moveOp = OpponentBuilder.builder(target)
                .setMove(opponentDto.move())
                .create();
        sendOpponentEvent(moveOp, "updated");

        Opponent opponent = OpponentBuilder.builder(target)
                .setMonster(updatedTarget.attributes().health() > 0 ? updatedTarget._id() : null)
                .setResults(results)
                .create();
        sendOpponentEvent(opponent, "updated");
    }

    public void setEvolves(boolean evolves) {
        this.evolves = evolves;
    }

    private boolean checkState() {
        if (encounterWrapper == null) {
            return true;
        }
        boolean attackerAlive = false;
        boolean defenderAlive = false;
        for (Opponent opponent : encounterWrapper.opponentByTrainer.values()) {
            if (opponent.monster() == null) {
                continue;
            }
            List<Monster> monsters = regionApi.getMonsters(RegionApiDummy.REGION_ID, opponent.trainer())
                    .blockingFirst();
            for (Monster monster : monsters) {
                if (monster.currentAttributes().health() > 0) {
                    if (opponent.isAttacker()) {
                        attackerAlive = true;
                    } else {
                        defenderAlive = true;
                    }
                }
            }
        }
        if (attackerAlive && defenderAlive) {
            return false;
        }
        deleteEncounter();
        return true;
    }

    private void deleteEncounter() {
        if (encounterWrapper == null) {
            return;
        }
        eventDummy.sendEvent(new Event<>("regions.%s.encounters.%s.deleted".formatted(
                RegionApiDummy.REGION_ID,
                encounterWrapper.encounter._id()),
                encounterWrapper.encounter));
        for (Opponent opponent : encounterWrapper.opponentByTrainer.values()) {
            sendOpponentEvent(opponent, "deleted");
        }
        encounterWrapper = null;
    }

    private void sendOpponentEvent(Opponent opponent, String event) {
        eventDummy.sendEvent(new Event<>("encounters.%s.trainers.%s.opponents.%s.%s"
                .formatted(opponent.encounter(), opponent.trainer(), opponent._id(), event), opponent));
        if (encounterWrapper != null) {
            encounterWrapper.opponentByTrainer.put(opponent.trainer(), opponent);
        }

    }

    @Override
    public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String opponentId) {
        if (encounterWrapper == null) {
            throw new IllegalStateException("Encounter not started");
        }
        if (regionId.isEmpty()) {
            return Observable.error(new Throwable(regionId + "does not exist"));
        }
        Opponent opponent = encounterWrapper.getOpponent(opponentId);
        deleteEncounter();
        return Observable.just(opponent);
    }

    private static class EncounterWrapper {
        private final Encounter encounter;
        private final Map<String, Opponent> opponentByTrainer = new LinkedHashMap<>();
        final Map<String, List<Encounter>> encounterHashMap = new HashMap<>();

        public EncounterWrapper(Encounter encounter, String opponentId, boolean big) {
            this.encounter = encounter;
            List<Opponent> opponentList = initDummyOpponent(encounter, opponentId, big);
            for (Opponent opponent : opponentList) {
                this.opponentByTrainer.put(opponent.trainer(), opponent);
            }
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
                    "4",
                    null,
                    List.of(),
                    0
            );
            return List.of(opponent0, opponent1, opponent2, opponent3);
        }

        public Opponent getOpponent(String id) {
            return this.opponentByTrainer.get(id);
        }

    }
}
