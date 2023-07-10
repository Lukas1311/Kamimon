package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.AbilityDto;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.EncounterService;
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
import java.util.List;

@Singleton
public class ActionFieldChooseOpponentController extends Controller {
    @FXML
    public Text chooseOpponentText;
    @FXML
    public HBox chooseOpponentBox;

    @Inject
    PresetService presetService;
    @Inject
    EncounterStorage encounterStorage;

    @Inject
    EncounterService encounterService;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public List<String> opponentMonstersList;

    private int optionIndex = 0;

    @Inject
    public ActionFieldChooseOpponentController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        addMonsterOption(null, null, true);

        Opponent opponent = encounterStorage.getSession().getOpponent(EncounterSlot.ENEMY_FIRST);
        Opponent opponent2 = encounterStorage.getSession().getOpponent(EncounterSlot.ENEMY_SECOND);

        if(opponent != null){
            subscribe(presetService.getMonster(opponent.monster()),
                    monsterDto -> addMonsterOption(opponent, monsterDto.name(), false));
        }
        if (opponent2 != null) {
            subscribe(presetService.getMonster(opponent2.monster()),
                    monsterDto -> addMonsterOption(opponent2, monsterDto.name(), false));
        }

        return parent;
    }

    public void addMonsterOption(Opponent opponent, String monsterName, boolean isBackOption) {
        HBox optionContainer = actionFieldControllerProvider.get()
                .getOptionContainer(isBackOption ? translateString("back") : monsterName);

        optionContainer.setOnMouseClicked(event -> {
            if (isBackOption){
                actionFieldControllerProvider.get().openChooseAbility();
            } else {
                actionFieldControllerProvider.get().setEnemyTrainerId(opponent.trainer());
                actionFieldControllerProvider.get().executeAbilityMove();
                actionFieldControllerProvider.get().openBattleLog();
            }
        });

        // each column containing a maximum of 2 options
        int index = optionIndex / 2;
        if (chooseOpponentBox.getChildren().size() <= index) {
            VBox vbox = new VBox();
            chooseOpponentBox.getChildren().add(vbox);
        }
        VBox vbox = (VBox) chooseOpponentBox.getChildren().get(index);

        // set IDs for the options
        int optionIndex = vbox.getChildren().size();
        optionContainer.setId("user_monster_" + (index * 2 + optionIndex));

        // if the option is 'Back', add it to the end of the VBox
        if (isBackOption) {
            vbox.getChildren().add(optionContainer);
        } else {
            vbox.getChildren().add(0, optionContainer);
        }

        this.optionIndex++;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
