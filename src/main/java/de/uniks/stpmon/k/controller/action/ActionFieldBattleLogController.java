package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.OpponentUpdate;
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
import java.util.Timer;

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



    private boolean encounterFinished = false;


    private Timer closeTimer;
    private boolean encounterClosingTextIsShown = false;
    private CloseEncounterTrigger closeEncounter;

    @Inject
    public ActionFieldBattleLogController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        battleLogService.setVBox(vBox);

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
            subscribe(sessionService.listenOpponent(slot), opp ->
                battleLogService.queueUpdate(new OpponentUpdate(slot, opp))
            );
        }
        onDestroy(battleLogService::clearService);
    }

    public void nextWindow() {
        battleLogService.showNextAction();
        //check if more updates need to be handled
        /*if (opponentUpdates.isEmpty()) {
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
                    //encounter over
                    sessionService.clearEncounter();
                    closeTimer.cancel();
                    // -------
                }


            }

        } else {
            //show the next update of opponent
            vBox.getChildren().clear();
            handleOpponentUpdate(opponentUpdates.get(0).getKey(), opponentUpdates.get(0).getValue());
            //handle results
            opponentUpdates.remove(0);
        }*/

    }

    public void endRound(boolean encounterIsOver){
        if(encounterIsOver){
            HybridController controller = hybridControllerProvider.get();
            app.show(controller);
            controller.openMain(MainWindow.INGAME);
            encounterFinished = false;
        } else {
            getActionField().openMainMenu();
        }
    }


    public void addTranslatedSection(String word, String... args) {
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

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
