package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldChooseAbilityController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public HBox chooseAbilityBox;

    @Inject
    PresetService presetService;

    @Inject
    Provider<ActionFieldMainMenuController> actionFieldMainMenuController;
    @Inject
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;
    @Inject
    ActionFieldChooseOpponentController actionFieldChooseOpponentController;

    public Monster monster;

    public String selectedAbility;
    public Integer maxUses;

    private int count = 0;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");

        monster = actionFieldChangeMonsterController.activeMonster;

        if(monster != null) {
            for (String key : monster.abilities().keySet()) {
                if (monster.abilities().containsKey(key)) {
                    setAction(key);
                }
            }
        }

        return parent;
    }

    public void setAction(String abilityId) {
        subscribe(presetService.getAbility(abilityId), ability -> {
            maxUses = ability.maxUses();
            addActionOption(ability.name());
        });
    }

    public void addActionOption(String option) {
        Text arrowText = new Text(" >");
        Text optionText = new Text(option);
        Text useLabel = new Text(" (??" + "/" + maxUses + ") ");

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText, useLabel);

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(option));

        // each column containing a maximum of 3 options
        int index = count / 3;
        if (chooseAbilityBox.getChildren().size() <= index) {
            VBox vbox = new VBox();
            chooseAbilityBox.getChildren().add(vbox);
        }
        VBox vbox = (VBox) chooseAbilityBox.getChildren().get(index);

        // set IDs for the options
        int optionIndex = vbox.getChildren().size();
        optionText.setId("ability_" + (index * 3 + optionIndex));

        vbox.getChildren().add(optionContainer);

        count++;
    }

    public void openAction(String option) {
        selectedAbility = option;
        pane.getChildren().add(actionFieldChooseOpponentController.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
