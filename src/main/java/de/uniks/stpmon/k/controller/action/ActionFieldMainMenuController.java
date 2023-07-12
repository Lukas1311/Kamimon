package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldMainMenuController extends Controller {

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    @FXML
    public Text textContent;
    @FXML
    public VBox mainMenuBox;

    @Inject
    public ActionFieldMainMenuController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();

        textContent.setText(translateString("wannaDo"));

        addActionOption(OptionType.FIGHT);
        addActionOption(OptionType.CHANGE_MON);

        //TODO: only call flee when wild encounter
        addActionOption(OptionType.FLEE);
        return parent;
    }

    public void addActionOption(OptionType option) {
        String optionText = switch (option) {
            case FIGHT -> "fight";
            case CHANGE_MON -> "changeMon";
            case FLEE -> "flee";
        };

        HBox optionContainer = actionFieldControllerProvider
                .get()
                .getOptionContainer(translateString(optionText));

        optionContainer.setOnMouseClicked(event -> openAction(option));

        int index = mainMenuBox.getChildren().size();
        optionContainer.getChildren().get(1).setId("main_menu_" + index);

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
        actionFieldControllerProvider.get().openFleeWildMonster();
    }

    public void openFight() {
        actionFieldControllerProvider.get().openChooseAbility();
    }

    public void openChangeMon() {
        actionFieldControllerProvider.get().openChangeMonster();
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

    public enum OptionType {
        FIGHT,
        CHANGE_MON,
        FLEE
    }


}
