package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.dto.IMove;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Result;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class OpponentBuilder {

    public static OpponentBuilder builder() {
        return new OpponentBuilder();
    }

    public static OpponentBuilder builder(Opponent opponent) {
        return builder().apply(opponent);
    }

    private String id;
    private String encounter;
    private String trainer;
    private boolean isAttacker;
    private boolean isNPC;
    private String monster;
    private IMove move;
    private final List<Result> results = new ArrayList<>();
    private int coins;

    private OpponentBuilder() {
    }

    public OpponentBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public OpponentBuilder setEncounter(String encounter) {
        this.encounter = encounter;
        return this;
    }

    public OpponentBuilder setTrainer(String trainer) {
        this.trainer = trainer;
        return this;
    }

    public OpponentBuilder setAttacker(boolean attacker) {
        isAttacker = attacker;
        return this;
    }

    public OpponentBuilder setNPC(boolean NPC) {
        isNPC = NPC;
        return this;
    }

    public OpponentBuilder setMonster(String monster) {
        this.monster = monster;
        return this;
    }

    public OpponentBuilder setMove(IMove move) {
        this.move = move;
        return this;
    }

    public OpponentBuilder setResults(List<Result> results) {
        this.results.clear();
        this.results.addAll(results);
        return this;
    }

    public OpponentBuilder addResult(Result result) {
        this.results.add(result);
        return this;
    }

    public OpponentBuilder setCoins(int coins) {
        this.coins = coins;
        return this;
    }

    private OpponentBuilder apply(Opponent opponent) {
        return setId(opponent._id())
                .setEncounter(opponent.encounter())
                .setTrainer(opponent.trainer())
                .setAttacker(opponent.isAttacker())
                .setNPC(opponent.isNPC())
                .setMonster(opponent.monster())
                .setMove(opponent.move())
                .setResults(opponent.results())
                .setCoins(opponent.coins());
    }

    public Opponent create() {
        return new Opponent(id, encounter, trainer, isAttacker, isNPC, monster, move, results, coins);
    }

}
