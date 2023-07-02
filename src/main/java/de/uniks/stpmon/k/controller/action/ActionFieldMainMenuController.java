package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

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
    InputHandler inputHandler;
    @Inject
    ActionFieldChooseOpponentController chooseOpponentController;
    @Inject
    ActionFieldChangeMonsterController changeMonsterController;
    @Inject
    EncounterOverviewController encounterOverviewController;

    private final List<HBox> actionOptions = new ArrayList<>();
    List<Monster> userMonsterList;

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
        HBox optionContainer = new HBox();
        actionOptions.add(optionContainer);

        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        optionContainer.getChildren().addAll(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(optionText));

        mainMenuBox.getChildren().add(optionContainer);
    }

    public void openAction(String option) {
        switch (option) {
            case "Fight" -> openFight();
            case "Change Mon" -> openChangeMon();
        }
    }

    private void openFight() {
        ActionFieldChooseOpponentController controller = new ActionFieldChooseOpponentController();
        pane.getChildren().add(controller.render());
    }

    private void openChangeMon() {
        ActionFieldChangeMonsterController controller = new ActionFieldChangeMonsterController();
        pane.getChildren().add(controller.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
