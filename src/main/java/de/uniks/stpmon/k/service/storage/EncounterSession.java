package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.DestructibleElement;
import de.uniks.stpmon.k.service.storage.cache.OpponentCache;
import de.uniks.stpmon.k.service.storage.cache.SingleMonsterCache;
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
    private final Map<String, SingleMonsterCache> cacheByOpponent = new HashMap<>();
    private final Set<EncounterMember> members = new LinkedHashSet<>();

    public EncounterSession(OpponentCache opponentCache) {
        this.opponentCache = opponentCache;
        this.attackerTeam = new LinkedList<>();
        this.ownTeam = new ArrayList<>();
        // Initialize with empty values to allow other teammates to be added after the self trainer
        this.ownTeam.add("");
    }

    public void setup(Provider<SingleMonsterCache> cacheProvider, String selfTrainer) {
        int teamIndex = 1;
        int attackerIndex = 0;
        // does not block because it is initialized with the initial values
        for (Opponent op : opponentCache.getCurrentValues()) {
            SingleMonsterCache monsterCache = cacheProvider.get();
            monsterCache.setup(op.trainer(), op.monster());
            monsterCache.init();
            cacheByOpponent.put(op._id(), monsterCache);
            EncounterMember member;
            // self trainer is always in the first position
            if (op.trainer().equals(selfTrainer)) {
                member = new EncounterMember(0, 0, false);
                ownTeam.set(0, op._id());
            } else if (!op.isAttacker()) {
                // other are added behind the self trainer
                member = new EncounterMember(teamIndex++, 0, false);
                ownTeam.add(op._id());
            } else {
                attackerTeam.add(op._id());
                member = new EncounterMember(attackerIndex++, 0, true);
            }
            members.add(member);
            listenToOpponentMonster(op._id(), member);
        }
        onDestroy(opponentCache::destroy);
    }

    private void listenToOpponentMonster(String opId, EncounterMember member) {
        onDestroy(opponentCache.listenValue(opId).subscribe((opponentOptional)->{
            if(opponentOptional.isEmpty()){
                return;
            }
            Opponent opponent = opponentOptional.get();
            SingleMonsterCache cache = cacheByOpponent.get(opId);
            Monster monster = cache.asNullable();
            if(monster != null && !opponent.monster().equals(monster._id())){
                cache.setup(cache.getTrainerId(), opponent.monster());
                cache.init();
            }
        }));
    }

    public boolean hasMember(EncounterMember member) {
        return members.contains(member);
    }

    public Monster getMonster(EncounterMember member) {
        String opponent = getOpponentId(member);
        if (opponent == null) {
            throw createOpponentNotFound(member);
        }
        SingleMonsterCache cache = cacheByOpponent.get(opponent);
        return cache == null ? null : cache.asNullable();
    }

    public Observable<Monster> listenMonster(EncounterMember member) {
        String opponent = getOpponentId(member);
        if (opponent == null) {
            return Observable.error(createOpponentNotFound(member));
        }
        SingleMonsterCache cache = cacheByOpponent.get(opponent);
        return cache == null ? Observable.empty() : cache.onValue().flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Observable<Opponent> listenOpponent(EncounterMember member) {
        String opponent = getOpponentId(member);
        if (opponent == null) {
            return Observable.error(createOpponentNotFound(member));
        }
        return opponentCache.listenValue(opponent).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty())
        );
    }

    public Opponent getOpponent(EncounterMember member) {
        String opponent = getOpponentId(member);
        if (opponent == null) {
            throw createOpponentNotFound(member);
        }
        return opponentCache.getValue(opponent).orElse(null);
    }

    private String getOpponentId(EncounterMember member) {
        if (member.index() < 0) {
            return null;
        }
        List<String> team = member.attacker() ? attackerTeam : ownTeam;
        return member.index() >= team.size() ? null : team.get(member.index());
    }

    private IllegalStateException createOpponentNotFound(EncounterMember member) {
        return new IllegalStateException(
                "Opponent not found: Member: %s, Own-Team: %s, Att-Team: %s"
                        .formatted(member, ownTeam, attackerTeam
                        ));
    }

    public List<String> getAttackerTeam() {
        return Collections.unmodifiableList(attackerTeam);
    }

    public List<String> getOwnTeam() {
        return Collections.unmodifiableList(ownTeam);
    }

    public CompletableSource waitForLoad() {
        return opponentCache.onInitialized()
                .andThen(Completable.merge(cacheByOpponent.values().stream()
                        .map(SingleMonsterCache::onInitialized).toList()));
    }

    public Collection<EncounterMember> getMembers() {
        return members;
    }
}
