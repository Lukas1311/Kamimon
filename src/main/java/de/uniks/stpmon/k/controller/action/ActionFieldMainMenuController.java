package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
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

    @FXML
    public Text textContent;
    @FXML
    public VBox mainMenuBox;

    public String fight;
    public String changeMon;

    @Inject
    public ActionFieldMainMenuController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();

        textContent.setText(translateString("wannaDo"));
        fight = translateString("fight");
        changeMon = translateString("changeMon");
        setActions();

        return parent;
    }

    public void setActions() {
        addActionOption(fight);
        addActionOption(changeMon);
    }

    public void addActionOption(String option) {
        Text arrowText = new Text(" >");
        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox();
        optionContainer.getChildren().addAll(arrowText, optionText);

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(option));

        int index = mainMenuBox.getChildren().size();
        optionText.setId("main_menu_" + index);

        mainMenuBox.getChildren().add(optionContainer);
    }

    public void openAction(String option) {
        if(option.equals(fight)) {
            openFight();
        } else if (option.equals(changeMon)) {
            openChangeMon();
        }
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
}
