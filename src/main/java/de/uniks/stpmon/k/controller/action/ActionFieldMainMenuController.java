package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    public ListView<HBox> mainMenuListView;

    @Inject
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;
    @Inject
    ActionFieldChooseAbilityController actionFieldChooseAbilityController;

    @Inject
    public ActionFieldMainMenuController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        textContent.setText("What do you want to do?");
        setAction();

        return parent;
    }

    public void setAction() {
        addActionOption("Fight");
        addActionOption("Change Mon");
    }

    public void addActionOption(String optionText) {
        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        HBox optionContainer = new HBox(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(optionText));

        int index = mainMenuListView.getItems().size();
        optionLabel.setId("main_menu_label_" + index);

        mainMenuListView.getItems().add(optionContainer);
    }

    public void openAction(String option) {
        switch (option) {
            case "Fight" -> openFight();
            case "Change Mon" -> openChangeMon();
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
