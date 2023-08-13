package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterState;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.DestructibleElement;
import de.uniks.stpmon.k.service.storage.cache.EncounterMember;
import de.uniks.stpmon.k.service.storage.cache.EncounterMonsters;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Provider;
import java.util.*;

/**
 * A session is a local representation of the encounter that contains all the data needed to play the encounter.
 * Including cached monsters and opponents.
 */
public class EncounterSession extends DestructibleElement {

    private final OpponentCache opponentCache;
    private final List<String> ownTeam;
    private final List<String> enemyTeam;
    private final Map<EncounterSlot, EncounterMember> cacheByOpponent = new HashMap<>();
    private EncounterMonsters allMonsterCache;
    private final List<EncounterSlot> slots = new ArrayList<>();
    /**
     * The id of the opponent that is currently joining the encounter.
     * This is used to switch the deleted with the newly created encounter.
     */
    private OpponentSwitch ongoingJoin = null;

    public EncounterSession(OpponentCache opponentCache) {
        this.opponentCache = opponentCache;
        this.enemyTeam = new LinkedList<>();
        this.ownTeam = new ArrayList<>();
        // Initialize with empty values to allow other teammates to be added after the self trainer
        this.ownTeam.add("");
    }

    public void setup(Provider<EncounterMember> cacheProvider,
                      Provider<EncounterMonsters> monstersProvider,
                      String selfTrainer) {
        int teamIndex = 1;
        int attackerIndex = 0;

        boolean attackingTeam = false;
        for (Opponent op : opponentCache.getCurrentValues()) {
            // self trainer is always in the first position
            if (op.trainer().equals(selfTrainer)) {
                attackingTeam = op.isAttacker();
                break;
            }
        }

        allMonsterCache = monstersProvider.get();
        Set<String> allTrainers = new HashSet<>();
        // does not block because it is initialized with the initial values
        for (Opponent op : opponentCache.getCurrentValues()) {
            EncounterMember monsterCache = cacheProvider.get();
            monsterCache.setup(op.trainer(), op.monster(), allMonsterCache);
            EncounterSlot member;
            boolean isOwnTrainer = op.trainer().equals(selfTrainer);
            // self trainer is always in the first position, only relevant if the trainer has only one opponent
            if (isOwnTrainer && ownTeam.get(0).isEmpty()) {
                member = new EncounterSlot(0, false);
                ownTeam.set(0, op._id());
            } else if (op.isAttacker() == attackingTeam) {
                // other are added behind the self trainer
                member = new EncounterSlot(teamIndex++, false);
                ownTeam.add(op._id());
            } else {
                enemyTeam.add(op._id());
                member = new EncounterSlot(attackerIndex++, true);
            }
            allTrainers.add(op.trainer());
            slots.add(member);
            cacheByOpponent.put(member, monsterCache);
            listenToOpponentMonster(op._id(), member);
        }
        allMonsterCache.setup(allTrainers);
        allMonsterCache.init();
        onDestroy(opponentCache::destroy);
        onDestroy(opponentCache.onCreation().subscribe(opponent -> {
            if (allTrainers.contains(opponent.trainer())) {
                return;
            }
            if (ongoingJoin == null) {
                ongoingJoin = new OpponentSwitch();
            }
            ongoingJoin.createdOpponent = opponent;
            tryJoin();
        }));
        onDestroy(opponentCache.onDeletion().subscribe(opponent -> {
            if (ongoingJoin == null) {
                ongoingJoin = new OpponentSwitch();
            }
            ongoingJoin.deletedOpponent = opponent;
            tryJoin();
        }));
    }

    private void listenToOpponentMonster(String opId, EncounterSlot slot) {
        onDestroy(opponentCache.listenValue(opId).subscribe((opponentOptional) -> {
            if (opponentOptional.isEmpty()) {
                return;
            }
            Opponent opponent = opponentOptional.get();
            EncounterMember cache = cacheByOpponent.get(slot);
            Monster monster = cache.asNullable();

            if (monster == null && opponent.monster() != null
                    || opponent.monster() != null && !Objects.equals(opponent.monster(), monster._id())) {
                cache.setup(cache.getTrainerId(), opponent.monster(), allMonsterCache);
                cache.init();
            }
        }));
    }

    private void tryJoin() {
        if (ongoingJoin == null) {
            return;
        }
        if (ongoingJoin.deletedOpponent == null || ongoingJoin.createdOpponent == null) {
            return;
        }
        if (Objects.equals(ongoingJoin.deletedOpponent, ongoingJoin.createdOpponent)) {
            return;
        }
        performJoin();
    }

    private void performJoin() {
        Opponent newOpponent = ongoingJoin.createdOpponent;
        EncounterMember member = cacheByOpponent.get(EncounterSlot.PARTY_SECOND);
        if (member == null) {
            return;
        }
        allMonsterCache.addTrainer(newOpponent.trainer());
        ownTeam.remove(ongoingJoin.deletedOpponent._id());
        ownTeam.add(newOpponent._id());
        member.setup(newOpponent.trainer(), newOpponent.monster(), allMonsterCache);
        member.init();
        ongoingJoin = null;
    }

    public boolean hasSlot(EncounterSlot slot) {
        return slots.contains(slot);
    }

    public String getTrainer(EncounterSlot slot) {
        EncounterMember cache = cacheByOpponent.get(slot);
        return cache == null ? null : cache.getTrainerId();
    }

    public MonsterState getMonsterState(EncounterSlot slot) {
        EncounterMember cache = cacheByOpponent.get(slot);
        if (cache == null) {
            return MonsterState.UNKNOWN;
        }
        Monster monster = cache.asNullable();
        // Currently the monster is switched out if it is dead
        if (monster == null) {
            return MonsterState.UNKNOWN;
        }
        return monster.currentAttributes().health() <= 0 ? MonsterState.DEAD : MonsterState.ALIVE;
    }

    public Monster getMonsterById(String id) {
        return allMonsterCache.getValue(id).orElse(null);
    }

    public Monster getMonster(EncounterSlot slot) {
        if (!hasSlot(slot)) {
            throw createOpponentNotFound(slot);
        }
        EncounterMember cache = cacheByOpponent.get(slot);
        return cache == null ? null : cache.asNullable();
    }

    public Observable<Monster> listenMonster(EncounterSlot slot) {
        if (!hasSlot(slot)) {
            return Observable.error(createOpponentNotFound(slot));
        }
        EncounterMember cache = cacheByOpponent.get(slot);
        return cache == null ? Observable.empty() : cache.onValue().flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<Opponent> listenOpponent(EncounterSlot slot) {
        String opponent = getOpponentId(slot);
        if (opponent == null) {
            return Observable.error(createOpponentNotFound(slot));
        }
        return opponentCache.listenValue(opponent).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public EncounterSlot getSlotForTrainer(String trainerId) {
        for (EncounterSlot slot : slots) {
            if (Objects.equals(getTrainer(slot), trainerId)) {
                return slot;
            }
        }
        return null;
    }

    public EncounterSlot getSlotForOpponent(String opponentId) {
        for (EncounterSlot slot : slots) {
            if (Objects.equals(getOpponentId(slot), opponentId)) {
                return slot;
            }
        }
        return null;
    }

    public Opponent getOpponent(EncounterSlot slot) {
        String opponent = getOpponentId(slot);
        if (opponent == null) {
            throw createOpponentNotFound(slot);
        }
        return opponentCache.getValue(opponent).orElse(null);
    }

    private String getOpponentId(EncounterSlot slot) {
        if (slot.partyIndex() < 0) {
            return null;
        }
        List<String> team = slot.enemy() ? enemyTeam : ownTeam;
        return slot.partyIndex() >= team.size() ? null : team.get(slot.partyIndex());
    }

    private IllegalStateException createOpponentNotFound(EncounterSlot slot) {
        return new IllegalStateException(
                "Opponent not found: Member: %s, Own-Team: %s, Att-Team: %s"
                        .formatted(slot, ownTeam, enemyTeam
                        ));
    }

    public List<String> getEnemyTeam() {
        return Collections.unmodifiableList(enemyTeam);
    }

    public List<String> getOwnTeam() {
        return Collections.unmodifiableList(ownTeam);
    }

    public CompletableSource waitForLoad() {
        return opponentCache.onInitialized()
                .andThen(allMonsterCache.onInitialized())
                .andThen(Completable.merge(cacheByOpponent.values().stream()
                        .map(EncounterMember::init)
                        .map((c) -> Completable.complete())
                        .toList()));
    }

    public Collection<EncounterSlot> getSlots() {
        return slots;
    }

    public Observable<Opponent> listenDeadOpponent(EncounterSlot slot) {
        return opponentCache.onDeletion().flatMap((opponent) -> {
            String id = getOpponentId(slot);
            if (ongoingJoin != null && ongoingJoin.createdOpponent != null) {
                return Observable.empty();
            }
            if (opponent._id().equals(id)) {
                return Observable.just(opponent);
            }
            return Observable.empty();
        });
    }

    private static class OpponentSwitch {
        Opponent deletedOpponent;
        Opponent createdOpponent;
    }
}
