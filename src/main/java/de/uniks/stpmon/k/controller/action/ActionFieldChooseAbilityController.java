package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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
    public ListView<HBox> changeAbilityListView;

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

    public void addActionOption(String optionText) {
        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);
        Label useLabel = new Label(" (??" + "/" + maxUses + ")");

        arrowLabel.setVisible(false);

        HBox optionContainer = new HBox(arrowLabel, optionLabel, useLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(optionText));

        int index = changeAbilityListView.getItems().size();
        optionLabel.setId("ability_label_" + index);

        changeAbilityListView.getItems().add(optionContainer);
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
