package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Singleton
public class ActionFieldChooseAbilityController extends Controller {

    @FXML
    public GridPane abilityGridPane;


    @Inject
    PresetService presetService;
    @Inject
    EncounterStorage encounterStorage;
    @Inject
    EncounterService encounterService;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public Monster monster;
    private int count = 0;

    @Inject
    public ActionFieldChooseAbilityController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        monster = encounterStorage.getSession().getMonster(new EncounterSlot(0, false));
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
            actionFieldControllerProvider.get().setAbilityId(ability.id());
            if(encounterStorage.getSession().getEnemyTeam().size() == 1){
                Opponent opponent = encounterStorage.getSession().getOpponent(EncounterSlot.ENEMY_FIRST);
                actionFieldControllerProvider.get().setEnemyTrainerId(opponent.trainer());
                actionFieldControllerProvider.get().openBattleLog();
                actionFieldControllerProvider.get().executeAbilityMove();
            }else{
                actionFieldControllerProvider.get().openChooseOpponent();
            }
        });

        // set IDs for the options
        optionContainer.getChildren().get(1).setId(ability.name());

        abilityGridPane.add(optionContainer, 0, count);
        count++;
    }

    public void addBackOption(String option) {
        HBox optionContainer = actionFieldControllerProvider.get().getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> actionFieldControllerProvider.get().openMainMenu());

        abilityGridPane.add(optionContainer, 1, 3);
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}