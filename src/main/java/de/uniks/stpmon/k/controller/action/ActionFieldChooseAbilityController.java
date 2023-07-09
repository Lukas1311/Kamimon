package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldChooseAbilityController extends Controller {
    @FXML
    public VBox abilityBox;

    @Inject
    PresetService presetService;
    @Inject
    EncounterStorage encounterStorage;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public Monster monster;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        monster = encounterStorage.getSession().getMonster(new EncounterSlot(0, false));

        for (String id : monster.abilities().keySet()) {
            addAbility(id, monster.abilities().get(id));
        }

        return parent;
    }

    public void addAbility(String abilityId, Integer remainingUses) {
        addBackOption(translateString("back"));
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
            actionFieldControllerProvider.get().setChosenAbility(ability);
            chooseAbility();
        });

        // set IDs for the options
        int optionIndex = abilityBox.getChildren().size();
        optionContainer.getChildren().get(1).setId("ability_" + optionIndex);

        System.out.println(optionContainer.getChildren().get(1).getId());

        abilityBox.getChildren().add(optionContainer);
    }

    public void chooseAbility() {
        if(encounterStorage.getSession().getAttackerTeam().size() == 1){
            actionFieldControllerProvider.get().openBattleLog();
        }else{
            actionFieldControllerProvider.get().openChooseOpponent();
        }
    }

    public void addBackOption(String option) {
        HBox optionContainer = actionFieldControllerProvider.get().getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> actionFieldControllerProvider.get().openMainMenu());

        abilityBox.getChildren().add(optionContainer);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}