package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.service.BattleLogService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ActionFieldBattleLogController extends BaseActionFieldController {

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public VBox vBox;
    @FXML
    public VBox battleLog;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;

    @Inject
    SessionService sessionService;

    @Inject
    PresetService presetService;
    @Inject
    InputHandler inputHandler;

    @Inject
    BattleLogService battleLogService;


    private final Map<EncounterSlot, MonsterTypeDto> attackedMonsters = new HashMap<>();
    private boolean encounterFinished = false;

    private final List<Map.Entry<EncounterSlot, Opponent>> opponentUpdates = new ArrayList<>();
    private Timer closeTimer;
    private boolean encounterClosingTextIsShown = false;
    private CloseEncounterTrigger closeEncounter;

    @Inject
    public ActionFieldBattleLogController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        encounterFinished = false;

        scrollPane.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                scrollPane.setVvalue(1.0);
            }
        });

        initListeners();

        scrollPane.setOnMouseReleased(event -> nextWindow());

        onDestroy(inputHandler.addPressedKeyHandler(event -> {
            if (event.getCode() == KeyCode.E) {
                nextWindow();
                event.consume();
            }
        }));

        return parent;
    }

    private void initListeners() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), opp -> {
                battleLogService.queueUpdate(slot, opp);




            });
        }
        onDestroy(lastOpponents::clear);
        onDestroy(attackedMonsters::clear);
    }

    public void nextWindow() {
        //check if more updates need to be handled
        if (opponentUpdates.isEmpty()) {
            if (encounterClosingTextIsShown) {
                //show text that user wins/loses
                vBox.getChildren().clear();
                addTextSection(translateString(closeEncounter.toString()));
                closeTimer = new Timer();
                closeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> nextWindow());
                    }
                }, (int) (effectContext.getEncounterAnimationSpeed() * 5f));
                encounterFinished = true;
                encounterClosingTextIsShown = false;
            } else {
                if (encounterFinished) {
                    if (hybridControllerProvider == null) {
                        return;
                    }
                    sessionService.clearEncounter();
                    closeTimer.cancel();
                    HybridController controller = hybridControllerProvider.get();
                    app.show(controller);
                    controller.openMain(MainWindow.INGAME);
                    encounterFinished = false;
                } else {
                    getActionField().openMainMenu();
                }
            }

        } else {
            //show the next update of opponent
            vBox.getChildren().clear();
            handleOpponentUpdate(opponentUpdates.get(0).getKey(), opponentUpdates.get(0).getValue());
            //handle results
            opponentUpdates.remove(0);
        }

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

    private void addTranslatedSection(String word, String... args) {
        addTextSection(translateString(word, args));
    }

    private void addTextSection(String text) {
        Label text1 = new Label(text + "\n");
        text1.setWrapText(true);
        text1.setMaxWidth(290);
        vBox.getChildren().add(text1);
    }

    public void closeEncounter(CloseEncounterTrigger closeEncounter) {
        encounterClosingTextIsShown = true;
        this.closeEncounter = closeEncounter;

    }



    /**
     *  Queues the updated opponents, so the user can click through the actions that happen in the encounter
     * @param slot
     * @param opp
     */
    private void queueOpponent(EncounterSlot slot, Opponent opp){
        if (sessionService.hasNoEncounter()) {
            return;
        }
        opponentUpdates.add(new AbstractMap.SimpleEntry<>(slot, opp));
        //check if this battle Log needs to start
        if(vBox.getChildren().size() == 0){
            nextWindow();
        }
    }

    private void handleOpponentUpdate(EncounterSlot slot, Opponent opp) {

        Opponent lastOpponent = lastOpponents.get(slot);
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

        encounterOverviewControllerProvider.get().showLevelUp();

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

    private AbilityDto getAbility(int id) {
        return presetService.getAbility(id).blockingFirst();
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
