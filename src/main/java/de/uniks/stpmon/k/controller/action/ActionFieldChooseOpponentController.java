package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
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
    SessionService sessionService;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    private int optionIndex = 0;

    @Inject
    public ActionFieldChooseOpponentController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        addMonsterOption(null, null, true);

        Monster monster1 = sessionService.getMonster(EncounterSlot.ENEMY_FIRST);
        Monster monster2 = sessionService.getMonster(EncounterSlot.ENEMY_SECOND);
        Opponent opponent1 = sessionService.getOpponent(EncounterSlot.ENEMY_FIRST);
        Opponent opponent2 = sessionService.getOpponent(EncounterSlot.ENEMY_SECOND);

        if(monster1 != null){
            subscribe(presetService.getMonster(monster1.type()),
                    monsterDto -> addMonsterOption(opponent1, monsterDto.name(), false));
        }
        if (monster2 != null) {
            subscribe(presetService.getMonster(monster2.type()),
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
                actionFieldControllerProvider.get().openBattleLog();
                actionFieldControllerProvider.get().executeAbilityMove();
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
