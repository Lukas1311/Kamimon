package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.dto.IMove;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Result;

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
    private Result result;
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

    public OpponentBuilder setResult(Result result) {
        this.result = result;
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
                .setResult(opponent.result())
                .setCoins(opponent.coins());
    }

    public Opponent create() {
        return new Opponent(id, encounter, trainer, isAttacker, isNPC, monster, move, result, coins);
    }

}
