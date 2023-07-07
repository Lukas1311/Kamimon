package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
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
    TrainerService trainerService;
    @Inject
    PresetService presetService;
    @Inject
    EncounterStorage encounterStorage;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public List<String> opponentMonstersList;

    private int count = 0;

    @Inject
    public ActionFieldChooseOpponentController(){

    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        //check if this screen is needed
        if(encounterStorage.getSession().getAttackerTeam().size() == 1){
            //TODO:make move
            // -> use actionFieldControllerProvider.get().getChosenAbility() to get ability

            actionFieldControllerProvider.get().openBattleLog();
        }


        //show all monsters of enemy
        //get team of enemy
        opponentMonstersList = encounterStorage.getSession().getAttackerTeam();
        addMonsters();

        return parent;
    }

    public void addMonsters() {
        if(opponentMonstersList != null) {
            for (String monster : opponentMonstersList) {
                subscribe(presetService.getMonster(monster), type -> addMonsterOption(type.name()));
            }
        }
    }

    public void addMonsterOption(String option) {
        Text arrowText = new Text(" >");
        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> {
            //TODO: make move
            showBattleLog();
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
        optionText.setId("opponent_monster_label_" + (index * 2 + optionIndex));

        vbox.getChildren().add(optionContainer);

        count++;
    }


    private void showBattleLog() {
        actionFieldControllerProvider.get().openBattleLog();
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
