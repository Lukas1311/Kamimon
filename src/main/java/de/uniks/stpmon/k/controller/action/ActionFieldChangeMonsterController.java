package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ActionFieldChangeMonsterController extends BaseActionFieldController {
    @FXML
    public Text textContent;
    @FXML
    public HBox changeMonBox;

    @Inject
    MonsterService monsterService;

    private Monster selectedUserMonster;
    private int count = 0;
    private String back;


    @Inject
    public ActionFieldChangeMonsterController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        back = translateString("back");

        textContent.setText(translateString("chooseMon"));

        showOptions();

        return parent;
    }

    public void showOptions() {
        count = 0;

        // Don't show the back option if the player monster is dead
        if (!getActionField().isOwnMonsterDead()) {
            addActionOption(back, true);
        }

        Monster primaryMonster = sessionService.getMonster(EncounterSlot.PARTY_FIRST);
        Monster secondaryMonster = null;
        if (sessionService.checkTrainer()) {
            secondaryMonster = sessionService.getMonster(EncounterSlot.PARTY_SECOND);
        }

        List<Monster> userMonstersList = monsterService.getTeam().blockingFirst();

        for (Monster monster : userMonstersList) {
            if (!sessionService.isMonsterDead(monster) && !primaryMonster._id().equals(monster._id())) {
                if (secondaryMonster != null && secondaryMonster._id().equals(monster._id())) {
                    continue;
                }
                subscribe(presetService.getMonster(monster.type()), type -> {
                    selectedUserMonster = monster;
                    addActionOption(monster._id() + " " + type.name(), false);
                });
            }
        }
    }

    public void addActionOption(String option, boolean isBackOption) {
        // id is in 0 and name in 1
        String[] idAndName = option.split(" ");

        HBox optionContainer;
        if (option.equals(back)) {
            optionContainer = ActionFieldController.getOptionContainer(option);
            optionContainer.setOnMouseClicked(event -> openAction(option));
        } else {
            optionContainer = ActionFieldController.getOptionContainer(idAndName[1]);
            optionContainer.setOnMouseClicked(event -> openAction(idAndName[0]));
        }

        // each column containing a maximum of 2 options
        int index = count / 3;
        if (changeMonBox.getChildren().size() <= index) {
            VBox vbox = new VBox();
            changeMonBox.getChildren().add(vbox);
        }
        VBox vbox = (VBox) changeMonBox.getChildren().get(index);

        // set IDs for the options
        int optionIndex = vbox.getChildren().size();
        optionContainer.setId("user_monster_" + (index * 3 + optionIndex));

        // if the option is 'Back', add it to the end of the VBox
        if (isBackOption) {
            vbox.getChildren().add(optionContainer);
        } else {
            vbox.getChildren().add(0, optionContainer);
        }

        count++;
    }

    public void openAction(String option) {
        if (option.equals(back)) {
            getActionField().openMainMenu();
        } else {
            getActionField().openBattleLog();
            getActionField().executeMonsterChange(selectedUserMonster);
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
