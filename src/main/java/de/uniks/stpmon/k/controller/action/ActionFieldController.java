package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.BattleLogService;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldController extends Controller {
    @FXML
    public StackPane actionFieldPane;
    @FXML
    public Pane actionFieldContent;
    @Inject
    Provider<ActionFieldMainMenuController> mainMenuControllerProvider;
    @Inject
    Provider<ActionFieldChooseAbilityController> chooseAbilityControllerProvider;
    @Inject
    Provider<ActionFieldChangeMonsterController> changeMonsterControllerProvider;
    @Inject
    Provider<ActionFieldBattleLogController> battleLogControllerProvider;
    @Inject
    Provider<ActionFieldChooseOpponentController> chooseOpponentControllerProvider;

    @Inject
    Provider<EncounterService> encounterServiceProvider;
    @Inject
    SessionService sessionService;
    @Inject
    BattleLogService battleLogService;

    private String enemyTrainerId;
    private int abilityId;
    private boolean ownMonsterDead;

    private Controller openController;
    private CloseEncounterTrigger closeTrigger;

    @Inject
    public ActionFieldController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(actionFieldPane, "action_menu_background.png");
        openMainMenu();

        checkDeadMonster();

        subscribe(sessionService.onEncounterCompleted(), () -> {
            // If user won or lost
            if (closeTrigger == null) {
                closeTrigger = sessionService.hasWon() ? CloseEncounterTrigger.WON : CloseEncounterTrigger.LOST;
            }

            closeEncounter(closeTrigger);
            closeTrigger = null;
        });

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

    public void closeEncounter(CloseEncounterTrigger closeEncounter) {
        openBattleLog();

        battleLogService.closeEncounter(closeEncounter);
    }

    public void openMainMenu() {
        if (ownMonsterDead) {
            open(changeMonsterControllerProvider);
            return;
        }
        open(mainMenuControllerProvider);
    }

    public void openChangeMonster(boolean dead) {
        setOwnMonsterDead(dead);
        // If battle log is open, don't open change monster, will be opened after battle log
        if (openController instanceof ActionFieldBattleLogController) {
            return;
        }
        open(changeMonsterControllerProvider);
    }

    public void openChooseAbility() {
        open(chooseAbilityControllerProvider);
    }

    public void openChooseOpponent() {
        open(chooseOpponentControllerProvider);
    }

    public void openBattleLog() {
        // Only open battle log if it's not already open
        if (openController instanceof ActionFieldBattleLogController) {
            return;
        }
        open(battleLogControllerProvider);
    }

    private <T extends Controller> void open(Provider<T> provider) {
        if (openController != null) {
            openController.destroy();
        }
        actionFieldContent.getChildren().clear();
        openController = provider.get();
        openController.init();
        actionFieldContent.getChildren().add(openController.render());
    }

    @Override
    public void destroy() {
        super.destroy();
        if (openController != null) {
            openController.destroy();
            openController = null;
        }
        ownMonsterDead = false;
    }

    public HBox getOptionContainer(String option) {
        Text arrowText = new Text(" >");

        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);
        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));

        return optionContainer;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public void setEnemyTrainerId(String trainerId) {
        this.enemyTrainerId = trainerId;
    }

    public void executeAbilityMove() {
        //TODO: chache old Monster
        subscribe(encounterServiceProvider.get()
                .makeAbilityMove(abilityId, enemyTrainerId));
    }

    public void checkDeadMonster() {
        subscribe(sessionService.listenOpponent(EncounterSlot.PARTY_FIRST), opponent -> {
            if (sessionService.isMonsterDead(EncounterSlot.PARTY_FIRST)) {
                openChangeMonster(true);
            }
        });
    }

    public void executeMonsterChange(Monster selectedMonster) {
        EncounterService encounterService = encounterServiceProvider.get();
        subscribe(ownMonsterDead ? encounterService.changeDeadMonster(selectedMonster) :
                encounterService.makeChangeMonsterMove(selectedMonster));
        setOwnMonsterDead(false);
    }

    public void setOwnMonsterDead(boolean ownMonsterDead) {
        this.ownMonsterDead = ownMonsterDead;
    }

    public boolean isOwnMonsterDead() {
        return ownMonsterDead;
    }

    protected void setFleeEncounter() {
        this.closeTrigger = CloseEncounterTrigger.FLEE;
        battleLogService.closeEncounter(closeTrigger);
        openBattleLog();
        battleLogService.showNextAction();
    }
}
