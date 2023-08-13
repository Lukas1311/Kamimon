package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.Monster;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActionFieldChooseAbilityController extends BaseActionFieldController {

    @FXML
    public GridPane abilityGridPane;

    public Monster activeMonster;
    private int count = 0;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        activeMonster = getActionField().getActiveMonster(true);

        addBackOption(translateString("back"));
        if (activeMonster != null) {
            for (String id : activeMonster.abilities().keySet()) {
                addAbility(id, activeMonster.abilities().get(id));
            }
        }
        count = 0;

        return parent;
    }

    public void addAbility(String abilityId, Integer remainingUses) {
        subscribe(presetService.getAbility(abilityId), abilityDto -> addAbilityOption(abilityDto, remainingUses));
    }

    public void addAbilityOption(AbilityDto ability, Integer remainingUses) {
        Text arrowText = new Text(" >");
        Text ablitiyNameText = new Text(ability.name());
        Text useLabel = new Text(" (" + remainingUses.toString() + "/" + ability.maxUses() + ") ");

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, ablitiyNameText, useLabel);
        optionContainer.setId("ability_" + ability.id());

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> {
            ActionFieldController actionFieldController = getActionField();
            actionFieldController.selectAbility(ability.id());
        });

        // set IDs for the options
        optionContainer.getChildren().get(1).setId(ability.name());

        abilityGridPane.add(optionContainer, 0, count);
        count++;
    }

    public void addBackOption(String option) {
        HBox optionContainer = ActionFieldController.getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> getActionField().openMainMenu());

        abilityGridPane.add(optionContainer, 1, 3);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}