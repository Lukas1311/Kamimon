package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.action.ActionFieldBattleLogController;
import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.encounter.LevelUp;
import de.uniks.stpmon.k.controller.monsters.MonsterInformationController;
import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.models.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Inject
    Provider<ActionFieldBattleLogController> battleLogControllerProvider;
    @Inject
    Provider<MonsterInformationController> monInfoProvider;

    @Inject
    protected EffectContext effectContext;


    VBox textBox;
    HashMap<EncounterSlot, String> monsterNames = new HashMap<>(4);


    private final Map<EncounterSlot, Opponent> lastOpponents = new HashMap<>();
    private final Map<EncounterSlot, MonsterTypeDto> attackedMonsters = new HashMap<>();
    private final List<OpponentUpdate> opponentUpdates = new ArrayList<>();

    private boolean encounterIsOver = false;
    /**
     * True if the user did his last move and is now waiting for the opponent to finish his move.
     */
    private boolean isWaiting = false;
    /**
     * True if the user is currently on the "waiting" screen.
     * This differs from {@link #encounterIsOver} because this is only true if the user can read the "waiting" text
     * in the battle log. {@link #encounterIsOver} can be true if the user still reads the last action of the opponent
     * for example.
     */
    private boolean currentlyWaiting = false;
    private boolean monsterCaught = false;
    private CloseEncounterTrigger closeEncounterTrigger = null;
    private Timer closeTimer;
    private final Map<EncounterSlot, Monster> monsBeforeLevelUp = new HashMap<>();
    private final Map<EncounterSlot, Monster> monsAfterLevelUp = new HashMap<>();

    private final Map<EncounterSlot, LevelUp> levelUps = new HashMap<>();
    public Item item;

    @Inject
    public BattleLogService() {

    }

    /**
     * This method is called by the listener in BattleLogController every time an update comes in
     *
     * @param slot     Slot of the updated opponent
     * @param opponent Updated opponent
     */
    public void queueUpdate(EncounterSlot slot, Opponent opponent) {
        // Skip first value because it is always the existing value
        if (!lastOpponents.containsKey(slot)) {
            // Cache the first value
            lastOpponents.put(slot, opponent);
            return;
        }

        queueOpponent(new OpponentUpdate(slot, opponent, lastOpponents.get(slot)));
        // Update the cached value
        lastOpponents.put(slot, opponent);
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
        List<Result> results = update.opponent().results();
        boolean wasWaiting = false;
        // Stop waiting if there are results
        if (!results.isEmpty()) {
            isWaiting = false;
            wasWaiting = currentlyWaiting;
            currentlyWaiting = false;
        }
        //check if this battleLog needs to start
        if (wasWaiting || textBox.getChildren().isEmpty()) {
            //shows next actions
            showActions();
        }
    }

    /**
     * This method is called to show what is going on in the Encounter via the BattleLog
     */
    public void showNextAction() {
        showActions();
    }

    /**
     * This method handles the queued updates and results
     */
    private void showActions() {
        textBox.getChildren().clear();
        //check if more results need to be shown
        Optional<EncounterSlot> slot = levelUps.keySet().stream().findFirst();
        if (slot.isPresent()) {
            LevelUp levelUp = levelUps.get(slot.get());
            String nextText = levelUp.getNextString();
            if (nextText != null) {
                battleLogControllerProvider.get().addTextSection(nextText);
                if (levelUp.showMonsterInformation()) {
                    encounterOverviewControllerProvider.get().showLevelUp(levelUp.getOldMon(), levelUp.getNewMon());
                }
                if (levelUp.playEvolutionAnimation()) {
                    ImageView node;
                    if (slot.get().partyIndex() == 1) {
                        node = encounterOverviewControllerProvider.get().userMonster1;
                    } else {
                        node = encounterOverviewControllerProvider.get().userMonster0;
                    }
                    //transition for monster evolution
                    TranslateTransition translation =
                            new TranslateTransition(Duration.millis(effectContext.getEncounterAnimationSpeed()), node);
                    translation.setByY(0);
                    translation.setByX(-1000);
                    translation.setAutoReverse(true);
                    translation.setCycleCount(2);
                    translation.play();


                    //change Image to new Monster
                    encounterOverviewControllerProvider.get().loadMonsterImage(levelUp.getNewMon().type().toString(), node, false);
                    battleLogControllerProvider.get().addTranslatedSection("monster-evolved", levelUp.getOldMonName(), levelUp.getNewMonName());

                    levelUp.setPlayEvolutionAnimation(false);
                }
                return;
            } else {
                //no more actions
                monsBeforeLevelUp.put(slot.get(), levelUp.getNewMon());
                monsterNames.put(slot.get(), levelUp.getNewMonName());
                levelUps.remove(slot.get());
            }
        }

        encounterOverviewControllerProvider.get().removeController("monInfo");
        //check if more opponentUpdates need to be shown
        if (!opponentUpdates.isEmpty()) {
            //there are more updates to be handled
            OpponentUpdate nextUpdate = opponentUpdates.get(0);
            if (nextUpdate != null) {
                handleOpponentUpdate(nextUpdate);
                opponentUpdates.remove(nextUpdate);
            }
            if (opponentUpdates.isEmpty() && isWaiting) {
                showInitialText();
            }
            // Run until there are no more updates or the user has to wait
            if (textBox.getChildren().isEmpty()) {
                showActions();
            }
        } else {
            //check if round or encounter is over
            if (closeEncounterTrigger == null) {
                if (isWaiting) {
                    currentlyWaiting = true;
                    addTranslatedSection("waiting.enemy");
                    return;
                }
                //round is over
                battleLogControllerProvider.get().endRound(false);
            } else {
                //init closing
                if (encounterIsOver) {
                    closeTimer.cancel();
                    shutDownEncounter();
                } else {
                    if (monsterCaught) {
                        closeEncounterTrigger = CloseEncounterTrigger.END;
                    }

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

    public void showInitialText() {
        if (!isWaiting) {
            return;
        }
        currentlyWaiting = true;
        addTranslatedSection("waiting.enemy");
    }

    private void shutDownEncounter() {
        encounterIsOver = false;
        closeEncounterTrigger = null;
        battleLogControllerProvider.get().endRound(true);
    }

    public void startWaiting() {
        isWaiting = true;
    }

    private void handleOpponentUpdate(OpponentUpdate up) {
        EncounterSlot slot = up.slot();
        Opponent opp = up.opponent();
        Opponent lastOpponent = up.lastOpponent();

        List<Result> results = opp.results();
        //check which move was made
        //check if monster was changed because of 0 hp
        if (lastOpponent != null && lastOpponent.monster() == null && opp.monster() != null) {
            Monster newMonster = sessionService.getMonsterById(opp.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
            return;
        }

        //check if user changed monster
        if (opp.move() instanceof ChangeMonsterMove move) {
            Monster newMonster = sessionService.getMonsterById(move.monster());
            addTranslatedSection("monster-changed", getMonsterType(newMonster.type()).name());
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
            Optional<Result> abilityResult = results.stream()
                    .filter(result -> result.type().equals("ability-success"))
                    .findFirst();
            if (abilityResult.isPresent()) {
                Result result = abilityResult.get();
                AbilityDto ability = getAbility(move.ability());
                if (result.status() != null && monster != null) {
                    addTranslatedSection("monsterAttacks.self", monsterNames.get(slot), ability.name());
                } else {
                    MonsterTypeDto eneMon = attackedMonsters.get(slot);
                    if (eneMon != null && monster != null && monsterNames.get(slot) != null) {
                        encounterOverviewControllerProvider.get().renderAttack(slot);
                        addTranslatedSection("monsterAttacks", monsterNames.get(slot), eneMon.name(), ability.name());
                    }
                    target = move.target();
                }
            }
        }

        for (Result result : results) {
            handleResult(monster, result, target, slot, opp);
        }
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
                //this is added because it should always be shown after the used attack
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
                if (!slot.enemy() && opp.coins() != 0) {
                    addTranslatedSection("earn-coins", opp.coins().toString());
                }
            }
            case "monster-changed" -> {
            }
            //called when not dying, e.g. another monster is available
            case "monster-defeated" -> addTranslatedSection("monster-defeated", monster.name());
            case "monster-levelup" -> {
                if (!slot.enemy()) {
                    createLevelUp(monster, slot);
                }
            }
            case "monster-evolved" -> {
                if (!slot.enemy()) {
                    levelUps.get(slot).setEvolved(result);
                }
            }
            case "monster-learned" -> {
                if (!slot.enemy()) {
                    levelUps.get(slot).setAttackLearned(result);
                }
            }
            case "monster-forgot" -> {
                if (!slot.enemy()) {
                    levelUps.get(slot).setAttackForgot(result);
                }
            }
            case "monster-dead" -> addTranslatedSection("monster-dead", monster.name());
            case "ability-unknown" ->
                    addTranslatedSection("ability-unknown", getAbility(ability).name(), monster.name());
            case "ability-no-uses" -> addTranslatedSection("ability-no-uses", getAbility(ability).name());
            case "ability-failed" -> addTranslatedSection("ability-failed", monster.name(), getAbility(ability).name());
            case "target-unknown" -> addTranslatedSection("target-unknown", monster.name());
            case "target-dead" -> addTranslatedSection("target-dead");
            case "item-failed" -> addTranslatedSection("item-failed", getItem(result.item()).name());
            case "item-success" -> {
                //item success is in result, if the call to use the item was successful
                //there will also be item-success if the monBall was used, but the mon was NOT caught
                //if the mon is caught, there is also a monster-caught result
                addTranslatedSection("item-success", getItem(result.item()).name());
                encounterOverviewControllerProvider.get().monBallAnimation(result.item());
            }
            case "status-added", "status-removed", "status-damage" -> addTranslatedSection("status."
                    + result.status().toString()
                    + result.type().replace("status-", "."), monster.name());
            case "monster-caught" -> {
                Monster mon = monsBeforeLevelUp.get(EncounterSlot.ENEMY_FIRST);
                MonsterTypeDto typeDto = getMonsterType(mon.type());
                addTranslatedSection("monster-caught", typeDto.name());
                monsterCaught = true;
            }
            default -> System.out.println("unknown result type");
        }
    }

    private void createLevelUp(MonsterTypeDto newMonType, EncounterSlot slot) {
        Monster oldMon = monsBeforeLevelUp.get(slot);
        Monster newMon = monsAfterLevelUp.get(slot);
        MonsterTypeDto oldMonType = getMonsterType(oldMon.type());

        LevelUp levelUp = new LevelUp(this, oldMon, newMon, oldMonType.name(), newMonType.name());
        levelUps.put(slot, levelUp);
    }

    public void setMonster(EncounterSlot slot, Monster mon) {
        //monster at start of encounter gets saved
        if (!monsBeforeLevelUp.containsKey(slot)) {
            monsBeforeLevelUp.put(slot, mon);
            monsterNames.put(slot, getMonsterType(mon.type()).name());
        } else {
            //level up
            monsAfterLevelUp.put(slot, mon);
        }
    }

    public String translate(String word, String... args) {
        return battleLogControllerProvider.get().translateString(word, args);
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

    public AbilityDto getAbility(int id) {
        return presetService.getAbility(id).blockingFirst();
    }

    private ItemTypeDto getItem(int id) {
        return presetService.getItem(id).blockingFirst();
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
        monsBeforeLevelUp.clear();
        monsAfterLevelUp.clear();
        monsterCaught = false;
        monsterNames.clear();
    }
}
