package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Result;
import de.uniks.stpmon.k.service.BattleLogService;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

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
    InputHandler inputHandler;

    @Inject
    BattleLogService battleLogService;

    @Inject
    public ActionFieldBattleLogController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        battleLogService.setVBox(vBox);

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
            subscribe(sessionService.listenOpponent(slot),
                    opp -> {
                        for (Result result : opp.results()) {
                            if (result.type().equals("monster-caught")) {
                                encounterOverviewControllerProvider.get().setCaught(true);
                            }
                        }
                        battleLogService.queueUpdate(slot, opp);
                    } );
            subscribe(sessionService.listenMonster(slot),
                    mon -> battleLogService.setMonster(slot, mon));
        }
        onDestroy(battleLogService::clearService);
    }

    @Override
    public void destroy() {
        super.destroy();
        battleLogService.setVBox(null);
    }

    public void nextWindow() {
        battleLogService.showNextAction();
    }

    public void endRound(boolean encounterIsOver) {
        if (encounterIsOver) {
            HybridController controller = hybridControllerProvider.get();
            app.show(controller);
            controller.openMain(MainWindow.INGAME);
            sessionService.clearEncounter();
        } else {
            getActionField().openMainMenu();
        }
    }

    public int getEffectContextTimerSpeed() {
        return (int) (effectContext.getEncounterAnimationSpeed() * 5f);
    }


    public void addTranslatedSection(String word, String... args) {
        addTextSection(translateString(word, args));
    }

    public void addTextSection(String text) {
        Label text1 = new Label(text + "\n");
        text1.setWrapText(true);
        text1.setMaxWidth(290);
        vBox.getChildren().add(text1);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
