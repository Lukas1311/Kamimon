package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.DestructibleElement;
import de.uniks.stpmon.k.service.storage.cache.EncounterMember;
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
    private final List<String> attackerTeam;
    private final Map<EncounterSlot, EncounterMember> cacheByOpponent = new HashMap<>();
    private final Set<EncounterSlot> slots = new LinkedHashSet<>();

    public EncounterSession(OpponentCache opponentCache) {
        this.opponentCache = opponentCache;
        this.attackerTeam = new LinkedList<>();
        this.ownTeam = new ArrayList<>();
        // Initialize with empty values to allow other teammates to be added after the self trainer
        this.ownTeam.add("");
    }

    public void setup(Provider<EncounterMember> cacheProvider, String selfTrainer) {
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

        // does not block because it is initialized with the initial values
        for (Opponent op : opponentCache.getCurrentValues()) {
            EncounterMember monsterCache = cacheProvider.get();
            monsterCache.setup(op.trainer(), op.monster());
            monsterCache.init();
            EncounterSlot member;
            // self trainer is always in the first position
            if (op.trainer().equals(selfTrainer)) {
                member = new EncounterSlot(0, false);
                ownTeam.set(0, op._id());
            } else if (op.isAttacker() == attackingTeam) {
                // other are added behind the self trainer
                member = new EncounterSlot(teamIndex++, false);
                ownTeam.add(op._id());
            } else {
                attackerTeam.add(op._id());
                member = new EncounterSlot(attackerIndex++, true);
            }
            slots.add(member);
            cacheByOpponent.put(member, monsterCache);
            listenToOpponentMonster(op._id(), member);
        }
        onDestroy(opponentCache::destroy);
    }

    private void listenToOpponentMonster(String opId, EncounterSlot slot) {
        onDestroy(opponentCache.listenValue(opId).subscribe((opponentOptional) -> {
            if (opponentOptional.isEmpty()) {
                return;
            }
            Opponent opponent = opponentOptional.get();
            EncounterMember cache = cacheByOpponent.get(slot);
            Monster monster = cache.asNullable();
            if (monster != null && !opponent.monster().equals(monster._id())) {
                cache.setup(cache.getTrainerId(), opponent.monster());
                cache.init();
            }
        }));
    }

    public boolean hasSlot(EncounterSlot slot) {
        return slots.contains(slot);
    }

    public String getTrainer(EncounterSlot slot) {
        EncounterMember cache = cacheByOpponent.get(slot);
        return cache == null ? null : cache.getTrainerId();
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

    public Completable onEncounterCompleted() {
        String opponent = getOpponentId(EncounterSlot.PARTY_FIRST);
        if (opponent == null) {
            return Completable.error(createOpponentNotFound(EncounterSlot.PARTY_FIRST));
        }
        return opponentCache.listenValue(opponent).filter(Optional::isEmpty).take(1).ignoreElements();
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
        List<String> team = slot.enemy() ? attackerTeam : ownTeam;
        return slot.partyIndex() >= team.size() ? null : team.get(slot.partyIndex());
    }

    private IllegalStateException createOpponentNotFound(EncounterSlot slot) {
        return new IllegalStateException(
                "Opponent not found: Member: %s, Own-Team: %s, Att-Team: %s"
                        .formatted(slot, ownTeam, attackerTeam
                        ));
    }

    public List<String> getEnemyTeam() {
        return Collections.unmodifiableList(attackerTeam);
    }

    public List<String> getOwnTeam() {
        return Collections.unmodifiableList(ownTeam);
    }

    public CompletableSource waitForLoad() {
        return opponentCache.onInitialized()
                .andThen(Completable.merge(cacheByOpponent.values().stream()
                        .map(EncounterMember::onInitialized).toList()));
    }

    public Collection<EncounterSlot> getSlots() {
        return slots;
    }
}
