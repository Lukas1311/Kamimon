package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
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
import java.util.Timer;
import java.util.TimerTask;

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
    PresetService presetService;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    InputHandler inputHandler;

    private boolean encounterFinished = false;
    private Timer closeTimer;

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

    public void nextWindow() {
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
        addTextSection(translateString(word, args), false);
    }

    private void addTextSection(String text, boolean overwriteAll) {
        //replace whole text in battle log
        if (overwriteAll) {
            vBox.getChildren().clear();
        }

        Label text1 = new Label(text + "\n");
        text1.setWrapText(true);
        text1.setMaxWidth(290);
        vBox.getChildren().add(text1);
    }

    public void closeEncounter(CloseEncounterTrigger closeEncounter) {
        addTextSection(translateString(closeEncounter.toString()), true);
        encounterFinished = true;
        closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> nextWindow());
            }
        }, (int) (effectContext.getEncounterAnimationSpeed() * 2.3f));
    }

    private void initListeners() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            // Skip first value because it is always the existing value
            subscribe(sessionService.listenOpponent(slot).skip(1), opp -> handleOpponentUpdate(slot, opp));
        }
    }

    private void handleOpponentUpdate(EncounterSlot slot, Opponent opp) {
        if (sessionService.hasNoEncounter()) {
            return;
        }
        MonsterTypeDto monster = getTypeForSlot(slot);

        if (monster == null) {
            return;
        }

        if (opp.move() instanceof AbilityMove move) {
            // Blocking can be used here because values are already loaded in the cache
            AbilityDto ability = getAbility(move.ability());
            EncounterSlot targetSlot = sessionService.getTarget(move.target());
            MonsterTypeDto eneMon = getTypeForSlot(targetSlot);

            if (eneMon == null) {
                return;
            }

            addTranslatedSection("monsterAttacks", monster.name(), eneMon.name(), ability.name());
            return;
        }

        if (opp.move() instanceof ChangeMonsterMove move) {
            subscribe(regionService.getMonster(regionStorage.getRegion()._id(), opp._id(), move.monster()), monster1 ->
                    addTranslatedSection("monster-changed", getMonsterType(monster1.type()).name()));
            return;
        }

        for (Result result : opp.results()) {
            handleResult(monster, result);
        }
    }

    private void handleResult(MonsterTypeDto monster, Result result) {
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
            case "target-defeated" -> addTranslatedSection("target-defeated", "Targeted monster");
            case "monster-changed" -> {
            }
            //called when not dying, e.g. another monster is available
            case "monster-defeated" -> addTranslatedSection("monster-defeated", monster.name());
            case "monster-levelup" -> addTranslatedSection("monster-levelup", monster.name(), "0");
            case "monster-evolved" -> addTranslatedSection("monster-evolved", monster.name());
            case "monster-learned" ->
                    addTranslatedSection("monster-learned", monster.name(), getAbility(ability).name());
            case "monster-dead" -> addTranslatedSection("monster-dead", monster.name());
            case "ability-unknown" ->
                    addTranslatedSection("ability-unknown", getAbility(ability).name(), monster.name());
            case "ability-no-uses" -> addTranslatedSection("ability-no-uses", getAbility(ability).name());
            case "target-unknown" -> addTranslatedSection("target-unknown");
            case "target-dead" -> addTranslatedSection("target-dead", "Targeted monster");
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
