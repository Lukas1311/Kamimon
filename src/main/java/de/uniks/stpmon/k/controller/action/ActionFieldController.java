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
    Provider<ActionFieldSelectMonController> selectMonControllerProvider;
    @Inject
    Provider<EncounterService> encounterServiceProvider;
    @Inject
    SessionService sessionService;
    @Inject
    BattleLogService battleLogService;

    private final Set<EncounterSlot> madeMoves = new HashSet<>();
    private final Set<EncounterSlot> deadOpponents = new HashSet<>();
    private String enemyTrainerId;
    private int abilityId;
    private boolean ownMonsterDead;
    private EncounterSlot activeSlot;
    private Controller openController;
    private CloseEncounterTrigger closeTrigger;
    private boolean monInfoOpen = false;


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

        subscribe(sessionService.onEncounterCompleted(), () -> {
            // If user won or lost
            if (closeTrigger == null) {
                closeTrigger = sessionService.hasWon() ? CloseEncounterTrigger.WON : CloseEncounterTrigger.LOST;
            }

            closeEncounter(closeTrigger);
            closeTrigger = null;
        });
        String trainer = sessionService.getTrainer(EncounterSlot.PARTY_FIRST);
        for (EncounterSlot slot : sessionService.getSlots()) {
            subscribe(sessionService.listenOpponent(slot), opponent -> {
                if (opponent == null) {
                    return;
                }
                if (opponent.results() != null && !opponent.results().isEmpty()) {
                    madeMoves.clear();
                }
                if (slot.enemy() || !opponent.trainer().equals(trainer)) {
                    return;
                }
                if (sessionService.isMonsterDead(slot)) {
                    deadOpponents.add(slot);
                    updateActiveSlot();
                    if (slot.equals(getActiveSlot())) {
                        openChangeMonster(true, false);
                    }
                } else {
                    if (deadOpponents.remove(slot)
                            && activeSlot.equals(slot)
                            && openController instanceof ActionFieldChangeMonsterController) {
                        setOwnMonsterDead(false);
                        // Update menu if it's open
                        openMainMenu();
                    }
                }
            });
        }

        return parent;
    }

    public boolean isMonInfoOpen() {
        return monInfoOpen;
    }

    public void setMonInfoOpen(boolean monInfoOpen) {
        this.monInfoOpen = monInfoOpen;
    }

    public void closeEncounter(CloseEncounterTrigger closeEncounter) {
        openBattleLog();

        battleLogService.closeEncounter(closeEncounter);
    }

    public void openMainMenu() {
        updateActiveSlot();
        if (deadOpponents.contains(getActiveSlot())) {
            openChangeMonster(true, true);
            return;
        }
        open(mainMenuControllerProvider);
    }

    public void openChangeMonster(boolean dead, boolean forced) {
        setOwnMonsterDead(dead);
        // If battle log is open, don't open change monster, will be opened after battle log
        if (!forced && openController instanceof ActionFieldBattleLogController) {
            return;
        }
        updateActiveSlot();
        open(changeMonsterControllerProvider);
    }

    public void openChooseAbility() {
        updateActiveSlot();
        open(chooseAbilityControllerProvider);
    }

    public void openInventory() {
        updateActiveSlot();
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

    public void openSelectMon() {
        open(selectMonControllerProvider);
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
        madeMoves.clear();
        deadOpponents.clear();
        enemyTrainerId = null;
        activeSlot = null;
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
        updateActiveSlot();
        EncounterSlot slot = getActiveSlot();
        madeMoves.add(slot);
        // Check if all moves are made, or we have to wait for enemy
        updateWaiting();
        // Show battle log if it's not already open
        openBattleLog();
        subscribe(encounterServiceProvider.get()
                .makeItemMove(getActiveSlot(), itemId, monsterId));

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


    private void retrieveActiveSlot() {
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

    public void updateActiveSlot() {
        if (sessionService.hasTwoActiveMonster()) {
            retrieveActiveSlot();
        } else {
            this.activeSlot = EncounterSlot.PARTY_FIRST;
        }
    }

    public Monster getActiveMonster(boolean updateSlot) {
        if (updateSlot) {
            updateActiveSlot();
        }
        return sessionService.getMonster(getActiveSlot());
    }

    public EncounterSlot getActiveSlot() {
        return activeSlot;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
