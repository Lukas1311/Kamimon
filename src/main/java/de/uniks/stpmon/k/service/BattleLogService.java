package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.action.ActionFieldBattleLogController;
import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.*;
import javafx.application.Platform;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class BattleLogService {
    @Inject
    SessionService sessionService;
    @Inject
    PresetService presetService;
    @Inject
    MonsterService monsterService;
    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Inject
    Provider<ActionFieldBattleLogController> battleLogControllerProvider;

    VBox textBox;


    private final Map<EncounterSlot, Opponent> lastOpponents = new HashMap<>();
    private final Map<EncounterSlot, MonsterTypeDto> attackedMonsters = new HashMap<>();
    private final List<OpponentUpdate> opponentUpdates = new ArrayList<>();

    private boolean encounterIsOver = false;
    private CloseEncounterTrigger closeEncounterTrigger = null;
    private Timer closeTimer;
    private final Map<EncounterSlot, Monster> monsBeforeLevelUp = new HashMap<>();
    private final Map<EncounterSlot, Monster> monsAfterLevelUp = new HashMap<>();

    @Inject
    public BattleLogService() {

    }

    /**
     * This method is called by the listener in BattleLogController every time an update comes in
     *
     * @param update OpponentUpdate
     */
    public void queueUpdate(OpponentUpdate update) {
        // Skip first value because it is always the existing value
        if (!lastOpponents.containsKey(update.slot())) {
            // Cache the first value
            lastOpponents.put(update.slot(), update.opponent());
            return;
        }

        queueOpponent(update);

    }

    /**
     * Queues the updated opponents, so the user can click through the actions that happen in the encounter
     *
     * @param update OpponentUpdate
     */
    private void queueOpponent(OpponentUpdate update) {
        if (sessionService.hasNoEncounter()) {
            return;
        }
        opponentUpdates.add(update);
        //check if this battleLog needs to start
        if (textBox.getChildren().isEmpty()) {
            //shows next actions
            showActions();
        }
    }

    private void showActions() {
        encounterOverviewControllerProvider.get().removeMonInfoIfShown();
        textBox.getChildren().clear();
        //check if more actions need to be handled
        if (!opponentUpdates.isEmpty()) {
            //there are more updates to be handled
            OpponentUpdate nextUpdate = opponentUpdates.get(0);
            //OpponentUpdate nextUpdate = getNextUpdate();
            if (nextUpdate != null) {
                handleOpponentUpdate(nextUpdate);
                opponentUpdates.remove(nextUpdate);
            }


        } else {
            //check if round or encounter is over
            if (closeEncounterTrigger == null) {
                //round is over
                battleLogControllerProvider.get().endRound(false);
            } else {
                //init closing
                if (encounterIsOver) {
                    closeTimer.cancel();
                    shutDownEncounter();
                } else { //user sees result of encounter
                    //show encounter result
                    addTranslatedSection(closeEncounterTrigger.toString());
                    encounterIsOver = true;
                    closeTimer = new Timer();
                    closeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> shutDownEncounter());
                        }
                    }, battleLogControllerProvider.get().getEffectContextTimerSpeed());
                }

            }
        }
    }

    private void shutDownEncounter() {
        encounterIsOver = false;
        closeEncounterTrigger = null;
        battleLogControllerProvider.get().endRound(true);
    }


    public void showNextAction() {
        showActions();
    }

    private void handleOpponentUpdate(OpponentUpdate up) {
        EncounterSlot slot = up.slot();
        Opponent opp = up.opponent();
        Opponent lastOpponent = lastOpponents.get(slot);

        //check which move was made
        //check if monster was changed because of 0 hp
        if (lastOpponent != null && lastOpponent.monster() == null && opp.monster() != null) {
            Monster newMonster = sessionService.getMonsterById(opp.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
            lastOpponents.put(up.slot(), up.opponent());
            return;
        }

        //check if user changed monster
        if (opp.move() instanceof ChangeMonsterMove move) {
            Monster newMonster = sessionService.getMonsterById(move.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
            lastOpponents.put(up.slot(), up.opponent());
            return;
        }

        // Save attacked monster before it is changed or dead
        if (opp.move() instanceof AbilityMove move) {
            EncounterSlot targetSlot = sessionService.getTarget(move.target());
            MonsterTypeDto eneMon = getTypeForSlot(targetSlot);
            if (eneMon != null) {
                attackedMonsters.put(slot, eneMon);
            } else {
                attackedMonsters.remove(slot);
            }
        }

        MonsterTypeDto monster = getTypeForSlot(slot);
        if (monster == null) {
            showNextAction();
        }

        String target = null;
        // Use last opponent to get the ability, this way we print the ability together with the result
        if (lastOpponent.move() instanceof AbilityMove move) {
            if (opp.results().stream().anyMatch(result -> result.type().equals("ability-success"))) {
                // Blocking can be used here because values are already loaded in the cache
                AbilityDto ability = getAbility(move.ability());
                MonsterTypeDto eneMon = attackedMonsters.get(slot);
                if (eneMon != null && monster != null) {
                    addTranslatedSection("monsterAttacks", monster.name(), eneMon.name(), ability.name());
                }
                target = move.target();
            }

        }

        for (Result result : opp.results()) {
            handleResult(monster, result, target, slot, opp);
        }

        lastOpponents.put(up.slot(), up.opponent());

    }

    private void handleResult(MonsterTypeDto monster, Result result, String target, EncounterSlot slot, Opponent opp) {

        final Integer ability = result.ability();
        switch (result.type()) {
            case "ability-success" -> {
                String translationVar = switch (result.effectiveness()) {
                    case "super-effective" -> "super-effective-atk";
                    case "effective" -> "effective-atk";
                    case "normal" -> "normal-atk";
                    case "ineffective" -> "ineffective-atk";
                    case "no-effect" -> "no-effect-atk";
                    default -> "";
                };
                addTranslatedSection(translationVar);
            }
            //when last monster is dead
            case "target-defeated" -> {
                String monsterName = "Targeted monster";
                if (target != null) {
                    MonsterTypeDto targetType = getTypeForSlot(sessionService.getTarget(target));
                    if (targetType != null) {
                        monsterName = targetType.name();
                    }
                }
                addTranslatedSection("target-defeated", monsterName, opp.coins().toString());
            }
            case "monster-changed" -> {
            }
            //called when not dying, e.g. another monster is available
            case "monster-defeated" -> addTranslatedSection("monster-defeated", monster.name());
            case "monster-levelup" -> makeLevelUp(monster, slot);
            case "monster-evolved" -> addTranslatedSection("monster-evolved", monster.name());
            case "monster-learned" ->
                    addTranslatedSection("monster-learned", monster.name(), getAbility(ability).name());
            case "monster-dead" -> addTranslatedSection("monster-dead", monster.name());
            case "ability-unknown" ->
                    addTranslatedSection("ability-unknown", getAbility(ability).name(), monster.name());
            case "ability-no-uses" -> addTranslatedSection("ability-no-uses", getAbility(ability).name());
            case "target-unknown" -> addTranslatedSection("target-unknown");
            case "target-dead" -> addTranslatedSection("target-dead");
            default -> System.out.println("unknown result type");
        }
    }

    private void makeLevelUp(MonsterTypeDto monster, EncounterSlot slot) {
        Monster oldMon = monsBeforeLevelUp.get(slot);
        Monster newMon = monsAfterLevelUp.get(slot);
        addTranslatedSection("monster-levelup", monster.name(), String.valueOf(newMon.level()));
        encounterOverviewControllerProvider.get().showLevelUp(oldMon, newMon);
        monsBeforeLevelUp.put(slot, newMon);
    }

    public void setMonster(EncounterSlot slot, Monster mon) {
        //monster at start of encounter gets safed
        if (!monsBeforeLevelUp.containsKey(slot)) {
            monsBeforeLevelUp.put(slot, mon);
        } else {
            //level up
            monsAfterLevelUp.put(slot, mon);
        }
    }

    private void addTranslatedSection(String word, String... args) {
        battleLogControllerProvider.get().addTranslatedSection(word, args);
    }

    private MonsterTypeDto getMonsterType(int id) {
        // Blocking can be used here because values are already loaded in the cache
        return presetService.getMonster(id).blockingFirst();
    }

    public MonsterTypeDto getTypeForSlot(EncounterSlot slot) {
        Monster monster = sessionService.getMonster(slot);
        if (monster == null) {
            return null;
        }
        // Blocking can be used here because values are already loaded in the cache
        return getMonsterType(monster.type());
    }

    private AbilityDto getAbility(int id) {
        return presetService.getAbility(id).blockingFirst();
    }

    public void setVBox(VBox vBox) {
        this.textBox = vBox;
    }

    public void closeEncounter(CloseEncounterTrigger trigger) {
        this.closeEncounterTrigger = trigger;
    }

    public void clearService() {
        lastOpponents.clear();
        attackedMonsters.clear();
    }
}
