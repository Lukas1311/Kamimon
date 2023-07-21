package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.action.ActionFieldBattleLogController;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.*;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class BattleLogService {
    @Inject
    SessionService sessionService;
    @Inject
    PresetService presetService;
    @Inject
    Provider<ActionFieldBattleLogController> battleLogControllerProvider;

    VBox textBox;


    private final Map<EncounterSlot, Opponent> lastOpponents = new HashMap<>();
    private final Map<EncounterSlot, MonsterTypeDto> attackedMonsters = new HashMap<>();
    private final List<OpponentUpdate> opponentUpdates = new ArrayList<>();

    private final boolean encounterIsOver = false;

    @Inject
    public BattleLogService() {

    }

    /**
     * This method is called by the listener in BattleLogController every time a update comes in
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
        lastOpponents.put(update.slot(), update.opponent());
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
        if (textBox.getChildren().size() == 0) {
            //shows next actions
            showActions();
        }
    }

    private void showActions() {
        //check if more actions need to be handled
        if (!opponentUpdates.isEmpty()) {
            //there are more updates to be handled
            OpponentUpdate nextUpdate = opponentUpdates.get(0);
            handleOpponentUpdate(nextUpdate);
            opponentUpdates.remove(0);

        } else {
            //check if round or encounter is over
            if (encounterIsOver) {

            }
        }
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
/*        if (lastOpponent != null && lastOpponent.monster() == null && opp.monster() != null) {
            Monster newMonster = sessionService.getMonsterById(opp.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
            return;
        }

        //check if user changed monster
        if (opp.move() instanceof ChangeMonsterMove move) {
            Monster newMonster = sessionService.getMonsterById(move.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
            return;
        }*/

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
            return;
        }

        String target = null;
        // Use last opponent to get the ability, this way we print the ability together with the result
        if (lastOpponent.move() instanceof AbilityMove move) {
            // Blocking can be used here because values are already loaded in the cache
            AbilityDto ability = getAbility(move.ability());
            MonsterTypeDto eneMon = attackedMonsters.get(slot);
            if (eneMon != null) {
                addTranslatedSection("monsterAttacks", monster.name(), eneMon.name(), ability.name());
            }
            target = move.target();
        }

        for (Result result : opp.results()) {
            //battleLogPages.add(new BattleLogEntry(monster, result, target));
            BattleLogEntry entry = new BattleLogEntry(monster, result, target);
            handleResult(entry);
        }
    }

    private void handleResult(BattleLogEntry battleLogEntry) {
        Result result = battleLogEntry.result();
        MonsterTypeDto monster = battleLogEntry.monster();
        String target = battleLogEntry.target();

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
                addTranslatedSection("target-defeated", monsterName);
            }
            case "monster-changed" -> {
            }
            //called when not dying, e.g. another monster is available
            case "monster-defeated" -> addTranslatedSection("monster-defeated", monster.name());
            case "monster-levelup" -> {
                addTranslatedSection("monster-levelup", monster.name(), "0");
                //showMonsterInformation();
            }
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

    private void addTranslatedSection(String word, String... args) {
        battleLogControllerProvider.get().addTranslatedSection(word, args);
    }

    private MonsterTypeDto getMonsterType(int id) {
        // Blocking can be used here because values are already loaded in the cache
        return presetService.getMonster(id).blockingFirst();
    }

    private MonsterTypeDto getTypeForSlot(EncounterSlot slot) {
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

    public void clearService() {
        lastOpponents.clear();
        attackedMonsters.clear();
    }
}
