package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActionFieldMainMenuController extends Controller {
    @FXML
    public Pane pane;
    @FXML
    public ImageView background;
    @FXML
    public Text textContent;
    @FXML
    public VBox mainMenuBox;

    @Inject
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;
    @Inject
    ActionFieldChooseAbilityController actionFieldChooseAbilityController;

    public String fight;
    public String changeMon;

    @Inject
    public ActionFieldMainMenuController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        textContent.setText(translateString("wannaDo"));
        fight = translateString("fight");
        changeMon = translateString("changeMon");
        setAction();

        return parent;
    }

    public void setAction() {
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
        pane.getChildren().add(actionFieldChooseAbilityController.render());
    }

    public void openChangeMon() {
        pane.getChildren().add(actionFieldChangeMonsterController.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
