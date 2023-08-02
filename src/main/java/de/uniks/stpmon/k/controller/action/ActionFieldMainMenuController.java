package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.controller.inventory.InventoryController;
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
            case ITEMS -> "inventory";
            case FLEE -> "flee";
        };

        HBox optionContainer = ActionFieldController.getOptionContainer(translateString(optionText));

        optionContainer.setOnMouseClicked(event -> openAction(option));

        optionContainer.setId("main_menu_" + optionText);

        if (optionText.equals("fight") || optionText.equals("changeMon")) {
            leftContainer.getChildren().add(optionContainer);
        } else {
            rightContainer.getChildren().add(optionContainer);
        }
    }

    public void openAction(OptionType option) {
        switch (option) {
            case CHANGE_MON -> openChangeMon();
            case FIGHT -> openFight();
            case ITEMS -> openInventory();
            case FLEE -> openFlee();
        }
    }

    public void openInventory() {
        if (encounterOverviewProvider.get().controller == null) {
            encounterOverviewProvider.get().actionFieldWrapperBox.setAlignment(Pos.BOTTOM_RIGHT);
            inventoryControllerProvider.get().isInEncounter = true;
            encounterOverviewProvider.get().openController("inventory", null);
        } else {
            inventoryControllerProvider.get().isInEncounter = false;
            encounterOverviewProvider.get().removeController("inventory");
        }
    }

    public void openFlee() {
        subscribe(encounterService.fleeEncounter(), opp -> getActionField().setFleeEncounter());
    }

    public void openFight() {
        getActionField().openChooseAbility();
    }

    public void openChangeMon() {
        getActionField().openChangeMonster(false);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

    public enum OptionType {
        FIGHT,
        CHANGE_MON,
        ITEMS,
        FLEE
    }


}
