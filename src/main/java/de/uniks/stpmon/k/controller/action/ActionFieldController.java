package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.encounter.CloseEncounterTrigger;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
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
import java.util.HashSet;
import java.util.Set;

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

    private final Set<EncounterSlot> madeMoves = new HashSet<>();
    private String enemyTrainerId;
    private int abilityId;
    private boolean ownMonsterDead;
    private EncounterSlot activeSlot;
    private Controller openController;
    private CloseEncounterTrigger closeTrigger;

    @Inject
    public ActionFieldController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(actionFieldPane, getResourcePath() + "ActionMenu.png");
        boolean canMakeMove = false;
        for (EncounterSlot slot : sessionService.getOwnSlots()) {
            Opponent opponent = sessionService.getOpponent(slot);
            if (opponent != null && opponent.move() == null) {
                canMakeMove = true;
            } else {
                madeMoves.add(slot);
            }
        }
        if (canMakeMove) {
            openMainMenu();
        } else {
            updateWaiting();
            openBattleLog();
        }

        checkDeadMonster();

        subscribe(sessionService.onEncounterCompleted(), () -> {
            // If user won or lost
            if (closeTrigger == null) {
                closeTrigger = sessionService.hasWon() ? CloseEncounterTrigger.WON : CloseEncounterTrigger.LOST;
            }

            closeEncounter(closeTrigger);
            closeTrigger = null;
        });
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), opponent -> {
                if (opponent == null) {
                    return;
                }
                if (opponent.results() != null && !opponent.results().isEmpty()) {
                    madeMoves.clear();
                }
            });
        }

        return parent;
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
        setActiveSlot();
        open(changeMonsterControllerProvider);
    }

    public void openChooseAbility() {
        setActiveSlot();
        open(chooseAbilityControllerProvider);
    }

    public void openInventory() {
        setActiveSlot();
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
        battleLogService.showInitialText();
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

    public static HBox getOptionContainer(String option) {
        Text arrowText = new Text(" >");

        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);
        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));

        return optionContainer;
    }

    public void selectAbility(int abilityId) {
        this.abilityId = abilityId;
        if (sessionService.getEnemyTeam().size() == 1) {
            Opponent opponent = sessionService.getOpponent(EncounterSlot.ENEMY_FIRST);
            setEnemyTrainerId(opponent.trainer());
            executeAbilityMove();
        } else {
            openChooseOpponent();
        }
    }

    public void selectEnemy(Opponent opponent) {
        setEnemyTrainerId(opponent.trainer());
        executeAbilityMove();
    }

    public void setEnemyTrainerId(String trainerId) {
        this.enemyTrainerId = trainerId;
    }

    public void updateWaiting() {
        if (madeMoves.isEmpty()) {
            return;
        }
        if (sessionService.hasTwoActiveMonster() && madeMoves.size() == 1) {
            return;
        }
        battleLogService.startWaiting();
    }

    public void executeAbilityMove() {
        madeMoves.add(getActiveSlot());
        // Check if all moves are made, or we have to wait for enemy
        updateWaiting();
        // Show battle log if it's not already open
        openBattleLog();
        subscribe(encounterServiceProvider.get()
                .makeAbilityMove(getActiveSlot(), abilityId, enemyTrainerId));
    }

    public void executeMonsterChange(Monster selectedMonster) {
        if (!ownMonsterDead) {
            madeMoves.add(getActiveSlot());
        }
        // Check if all moves are made, or we have to wait for enemy
        updateWaiting();
        // Show battle log if it's not already open
        openBattleLog();
        EncounterService encounterService = encounterServiceProvider.get();
        subscribe(ownMonsterDead ? encounterService.changeDeadMonster(getActiveSlot(), selectedMonster) :
                encounterService.makeChangeMonsterMove(getActiveSlot(), selectedMonster));
        setOwnMonsterDead(false);
    }

    public void executeItemMove(int itemId, String monsterId) {
        setActiveSlot();
        EncounterSlot slot = getActiveSlot();
        madeMoves.add(slot);
        // Check if all moves are made, or we have to wait for enemy
        updateWaiting();
        // Show battle log if it's not already open
        openBattleLog();
        subscribe(encounterServiceProvider.get()
                .makeItemMove(getActiveSlot(), itemId, monsterId));

    }


    public void checkDeadMonster() {
        subscribe(sessionService.listenOpponent(EncounterSlot.PARTY_FIRST), opponent -> {
            if (sessionService.isMonsterDead(EncounterSlot.PARTY_FIRST)) {
                openChangeMonster(true);
            }
        });
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


    private void updateActiveSlot() {
        for (EncounterSlot slot : sessionService.getSlots()) {
            if (slot.enemy()) {
                continue;
            }
            Opponent opponent = sessionService.getOpponent(slot);
            if (opponent != null && opponent.move() == null) {
                this.activeSlot = slot;
                break;
            }
        }
    }

    public void setActiveSlot() {
        if (sessionService.hasTwoActiveMonster()) {
            updateActiveSlot();
        } else {
            this.activeSlot = EncounterSlot.PARTY_FIRST;
        }
    }

    public EncounterSlot getActiveSlot() {
        return activeSlot;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
