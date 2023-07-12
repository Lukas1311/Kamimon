package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
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
    Provider<ActionFieldController> actionFieldControllerProvider;

    public List<String> opponentMonstersList;

    private int count = 0;

    public String back;

    @Inject
    public ActionFieldChooseOpponentController(){
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        back = translateString("back");

        //show all monsters of enemy
        //get team of enemy
        opponentMonstersList = encounterStorage.getSession().getAttackerTeam();

        addMonsters();

        return parent;
    }

    public void addMonsters() {
        count = 0;
        addMonsterOption(back, true);

        if(opponentMonstersList != null) {
            for (String monster : opponentMonstersList) {
                subscribe(presetService.getMonster(monster), type -> addMonsterOption(type.name(), false));
            }
        }
    }

    public void addMonsterOption(String option, boolean isBackOption) {
        HBox optionContainer = actionFieldControllerProvider.get().getOptionContainer(option);

        optionContainer.setOnMouseClicked(event -> {
            //TODO: make move
            showBattleLog(option);
        });

        // each column containing a maximum of 2 options
        int index = count / 2;
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

        count++;
    }

    private void showBattleLog(String option) {
        if (option.equals(back)) {
            actionFieldControllerProvider.get().openChooseAbility();
        } else {
            actionFieldControllerProvider.get().openBattleLog();
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}