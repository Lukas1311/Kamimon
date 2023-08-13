package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
import de.uniks.stpmon.k.models.Monster;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.action.ActionFieldMainMenuController.OptionType.*;

@Singleton
public class ActionFieldMainMenuController extends BaseActionFieldController {
    @FXML
    public Text textContent;
    @FXML
    public HBox mainMenuBox;
    @FXML
    public VBox leftContainer;
    @FXML
    public VBox rightContainer;

    @Inject
    Provider<EncounterOverviewController> encounterOverviewProvider;
    @Inject
    Provider<InventoryController> inventoryControllerProvider;

    @Inject
    public ActionFieldMainMenuController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        textContent.setText(translateString("wannaDo"));

        addActionOption(FIGHT);
        addActionOption(CHANGE_MON);
        addActionOption(SHOW_INFO);
        addActionOption(ITEMS);

        if (encounterStorage.getEncounter().isWild()) {
            addActionOption(FLEE);
        }

        return parent;
    }


    public void addActionOption(OptionType option) {
        String optionText = switch (option) {
            case FIGHT -> "fight";
            case CHANGE_MON -> "changeMon";
            case SHOW_INFO -> "showInfo";
            case ITEMS -> "inventory";
            case FLEE -> "flee";
        };

        HBox optionContainer = ActionFieldController.getOptionContainer(translateString(optionText));

        optionContainer.setOnMouseClicked(event -> openAction(option));

        optionContainer.setId("main_menu_" + optionText);

        if (optionText.equals("fight") || optionText.equals("changeMon") || optionText.equals("flee")) {
            leftContainer.getChildren().add(optionContainer);
        } else {
            rightContainer.getChildren().add(optionContainer);
        }
    }

    public void openAction(OptionType option) {
        if (getActionField().isMonInfoOpen()) {
            encounterOverviewProvider.get().removeMonInfo();
            getActionField().setMonInfoOpen(false);
        }
        switch (option) {
            case CHANGE_MON -> openChangeMon();
            case FIGHT -> openFight();
            case SHOW_INFO -> openInfo();
            case ITEMS -> openInventory();
            case FLEE -> openFlee();
        }
    }

    public void openInventory() {
        getActionField().openInventory();
        if (encounterOverviewProvider.get().controller == null) {
            encounterOverviewProvider.get().actionFieldWrapperBox.setAlignment(Pos.BOTTOM_RIGHT);
            inventoryControllerProvider.get().setInEncounter(true);
            encounterOverviewProvider.get().openController("inventory", null);
        } else {
            inventoryControllerProvider.get().setInEncounter(false);
            encounterOverviewProvider.get().removeController("inventory");
        }
    }

    public void openInfo() {
        Monster activeMon = getActionField().getActiveMonster(true);
        encounterOverviewProvider.get().showMonInfo(activeMon);
        getActionField().setMonInfoOpen(true);
        getActionField().openSelectMon();
    }

    public void openFlee() {
        encounterOverviewProvider.get().removeController("all");
        subscribe(encounterService.fleeEncounter(), opp -> getActionField().setFleeEncounter());
    }

    public void openFight() {
        encounterOverviewProvider.get().removeController("all");
        getActionField().openChooseAbility();
    }

    public void openChangeMon() {
        encounterOverviewProvider.get().removeController("all");
        getActionField().openChangeMonster(false, false);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

    public enum OptionType {
        FIGHT,
        CHANGE_MON,
        SHOW_INFO,
        ITEMS,
        FLEE
    }


}
