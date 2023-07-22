package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldMainMenuController extends BaseActionFieldController {
    @FXML
    public Text textContent;
    @FXML
    public VBox mainMenuBox;

    @Inject
    Provider<EncounterOverviewController> encounterOverviewProvider;

    @Inject
    public ActionFieldMainMenuController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        textContent.setText(translateString("wannaDo"));

        addActionOption(OptionType.FIGHT);
        addActionOption(OptionType.CHANGE_MON);

        if (encounterStorage.getEncounter().isWild()) {
            addActionOption(OptionType.FLEE);
        }

        return parent;
    }


    public void addActionOption(OptionType option) {
        String optionText = switch (option) {
            case FIGHT -> "fight";
            case CHANGE_MON -> "changeMon";
            case FLEE -> "flee";
        };

        HBox optionContainer = getActionField()
                .getOptionContainer(translateString(optionText));

        optionContainer.setOnMouseClicked(event -> openAction(option));

        optionContainer.setId("main_menu_" + optionText);

        mainMenuBox.getChildren().add(optionContainer);
    }

    public void openAction(OptionType option) {
        switch (option) {
            case CHANGE_MON -> openChangeMon();
            case FIGHT -> openFight();
            case FLEE -> openFlee();
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

    private enum OptionType {
        FIGHT,
        CHANGE_MON,
        FLEE
    }


}
