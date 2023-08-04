package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.service.BattleLogService;

import java.util.ArrayList;
import java.util.List;

public class LevelUp {

    BattleLogService battleLogService;
    private Monster oldMon;
    private Monster newMon;
    private String oldMonName;
    private String newMonName;
    private int newLevel;

    private final List<Result> results = new ArrayList<>();
    private Result evolved;
    private Result attackLearned;
    private Result attackForgot;
    private boolean levelReachedShown = false;
    private boolean playEvolutionAnimation = false;

    public LevelUp(BattleLogService battleLogService, Monster oldMon, Monster newMon, String oldMonName, String newMonName) {
        this.battleLogService = battleLogService;
        this.oldMon = oldMon;
        this.newMon = newMon;
        this.oldMonName = oldMonName;
        this.newMonName = newMonName;
        this.newLevel = newMon.level();
    }

    public Monster getOldMon() {
        return oldMon;
    }

    public void setOldMon(Monster oldMon) {
        this.oldMon = oldMon;
    }

    public Monster getNewMon() {
        return newMon;
    }

    public void setNewMon(Monster newMon) {
        this.newMon = newMon;
    }

    public String getOldMonName() {
        return oldMonName;
    }

    public void setOldMonName(String oldMonName) {
        this.oldMonName = oldMonName;
    }

    public String getNewMonName() {
        return newMonName;
    }

    public void setNewMonName(String newMonName) {
        this.newMonName = newMonName;
    }

    public int getLevel() {
        return newLevel;
    }

    public void setLevelReached(int lvl) {
        this.newLevel = lvl;
    }

    public void setEvolved(Result evolved) {
        this.evolved = evolved;
    }


    public void setAttackLearned(Result attackLearned) {
        this.attackLearned = attackLearned;
    }

    public void setAttackForgot(Result attackForgot) {
        this.attackForgot = attackForgot;
    }

    public void setLevelReachedShown(boolean wasShown) {
        this.levelReachedShown = wasShown;
    }

    public boolean getLevelReachedShown() {
        return this.levelReachedShown;
    }

    public void setPlayEvolutionAnimation(boolean playEvolutionAnimation) {
        this.playEvolutionAnimation = playEvolutionAnimation;
    }

    public boolean playEvolutionAnimation() {
        return this.playEvolutionAnimation;
    }

    /**
     * Get the next level up related String
     *
     * @return null if no more actions to show
     */
    public String getNextString() {
        if (!levelReachedShown) {
            levelReachedShown = true;
            return battleLogService.translate("monster-levelup", oldMonName, String.valueOf(newLevel));
        }
        //check if new attack was learned
        if (attackLearned != null && attackLearned.ability() != null) {
            String text = battleLogService.translate("monster-learned", oldMonName,
                    battleLogService.getAbility(attackLearned.ability()).name());
            attackLearned = null;
            if (attackForgot != null && attackForgot.ability() != null) {
                text += "\n" + battleLogService.translate("monster-forgot", oldMonName,
                        battleLogService.getAbility(attackForgot.ability()).name());
                attackForgot = null;
            }
            return text;
        }

        if (evolved != null) {
            evolved = null;
            playEvolutionAnimation = true;
            return battleLogService.translate("monster-evolves", oldMonName);
        }

        return null;
    }

    public boolean showMonsterInformation() {
        return attackLearned == null && evolved == null;
    }
}
