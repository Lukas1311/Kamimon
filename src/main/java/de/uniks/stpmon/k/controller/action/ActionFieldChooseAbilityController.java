package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
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

    public Monster monster;
    private int count = 0;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        monster = sessionService.getMonster(new EncounterSlot(0, false));
        addBackOption(translateString("back"));
        for (String id : monster.abilities().keySet()) {
            addAbility(id, monster.abilities().get(id));
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

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> {
            ActionFieldController actionFieldController = getActionField();
            actionFieldController.setAbilityId(ability.id());
            if (sessionService.getEnemyTeam().size() == 1) {
                Opponent opponent = sessionService.getOpponent(EncounterSlot.ENEMY_FIRST);
                actionFieldController.setEnemyTrainerId(opponent.trainer());
                actionFieldController.openBattleLog();
                actionFieldController.executeAbilityMove();
            } else {
                actionFieldController.openChooseOpponent();
            }
        });

        // set IDs for the options
        optionContainer.getChildren().get(1).setId(ability.name());

        abilityGridPane.add(optionContainer, 0, count);
        count++;
    }

    public void addBackOption(String option) {
        HBox optionContainer = getActionField().getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> getActionField().openMainMenu());

        abilityGridPane.add(optionContainer, 1, 3);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}