package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.MonsterInformationController;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.AbilityMove;
import de.uniks.stpmon.k.dto.ChangeMonsterMove;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.service.*;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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
public class ActionFieldBattleLogController extends Controller {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public VBox vBox;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    @Inject
    Provider<HybridController> hybridControllerProvider;

    @Inject
    SessionService sessionService;

    @Inject
    PresetService presetService;

    @Inject
    EncounterStorage encounterStorage;

    @Inject
    TrainerStorage trainerService;

    @Inject
    MonsterService monsterService;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    MonsterInformationController monsterInformationController;
    @Inject
    EncounterOverviewController encounterOverviewController;

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
        showMonsterInformation();

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
            closeTimer.cancel();
            HybridController controller = hybridControllerProvider.get();
            app.show(controller);
            controller.openMain(MainWindow.INGAME);
            encounterFinished = false;
        } else {
            actionFieldControllerProvider.get().openMainMenu();
        }
    }


    private MonsterTypeDto getTypeForSlot(EncounterSlot slot) {
        Monster monster = sessionService.getMonster(slot);
        if (monster == null) {
            return null;
        }
        // Blocking can be used here because values are already loaded in the cache
        return presetService.getMonster(monster.type()).blockingFirst();
    }


    public void addTextSection(String text, boolean overwriteAll) {
        //replace whole text in battle log
        if (overwriteAll) {
            vBox.getChildren().clear();
        }

        Label text1 = new Label(text + "\n");
        text1.setWrapText(true);
        text1.setMaxWidth(290);
        vBox.getChildren().add(text1);
    }

    public void closeEncounter(EncounterService.CloseEncounter closeEncounter){
        addTextSection(translateString(closeEncounter.toString()), true);
        encounterFinished = true;
        closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> nextWindow());
            }
        }, 1300);

    }

    public void showMonsterInformation() {
        Monster monster = encounterStorage.getSession().getMonster(EncounterSlot.PARTY_FIRST);
        monsterInformationController.loadMonsterTypeDto(String.valueOf(monster.type()));
        monsterInformationController.loadMonster(monster);
        encounterOverviewController.monsterInformationBox.setVisible(true);
    }

    private void initListeners() {

        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot).skip(1), opp -> {

                if (encounterStorage.getSession() == null) {
                    return;
                }
                MonsterTypeDto monster = getTypeForSlot(slot);

                if (monster == null) {
                    return;
                }

                if (opp.move() instanceof AbilityMove move) {
                    // Blocking can be used here because values are already loaded in the cache
                    AbilityDto ability = presetService.getAbility(move.ability()).blockingFirst();
                    EncounterSlot targetSlot = sessionService.getTarget(((AbilityMove) opp.move()).target());
                    MonsterTypeDto eneMon = getTypeForSlot(targetSlot);

                    if (eneMon == null) {
                        return;
                    }

                    addTextSection(translateString("monsterAttacks", monster.name(), eneMon.name(), ability.name()), false);
                    return;
                }

                if (opp.move() instanceof ChangeMonsterMove move) {
                    subscribe(regionService.getMonster(regionStorage.getRegion()._id(), opp._id(), move.monster()), monster1 -> {
                                addTextSection(translateString("monster-changed",
                                        presetService.getMonster(monster1.type()).blockingFirst().name()), false);
                            }
                    );
                    return;
                }

                for (Result result : opp.results()) {
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
                            addTextSection(translateString(translationVar), false);
                        }

                        case "target-defeated" ->
                                addTextSection(translateString("target-defeated", "Targeted monster"), false); //when last monster is dead
                        case "monster-changed" -> {}
                        case "monster-defeated" ->
                                addTextSection(translateString("monster-defeated", monster.name()), false); //called when not dying, eg another monster is available
                        case "monster-levelup" -> {
                            // TODO: add the change of exp
                            //showMonsterInformation();
                            addTextSection(translateString("monster-levelup", monster.name(), "0"), false);
                        }
                        case "monster-evolved" ->
                                addTextSection(translateString("monster-evolved", monster.name()), false);
                        case "monster-learned" ->
                                addTextSection(translateString("monster-learned", monster.name(), presetService.getAbility(result.ability()).blockingFirst().name()), false);
                        case "monster-dead" -> addTextSection(translateString("monster-dead", monster.name()), false);
                        case "ability-unknown" ->
                                addTextSection(translateString("ability-unknown", presetService.getAbility(result.ability()).blockingFirst().name(), monster.name()), false);
                        case "ability-no-uses" ->
                                addTextSection(translateString("ability-no-uses", presetService.getAbility(result.ability()).blockingFirst().name()), false);
                        case "target-unknown" -> addTextSection(translateString("target-unknown"), false);
                        case "target-dead" -> addTextSection(translateString("target-dead", "Targeted monster"), false);
                        default -> System.out.println("unknown result type");
                    }
                }
            });
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
