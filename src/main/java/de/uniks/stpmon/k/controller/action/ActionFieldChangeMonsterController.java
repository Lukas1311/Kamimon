package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.*;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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
public class ActionFieldChangeMonsterController extends Controller {
    @FXML
    public Text textContent;
    @FXML
    public HBox changeMonBox;

    @Inject
    MonsterService monsterService;
    @Inject
    PresetService presetService;
    @Inject
    RegionService regionService;
    @Inject
    EncounterService encounterService;
    @Inject
    EncounterStorage encounterStorage;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    SessionService sessionService;

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;

    public List<Monster> userMonstersList;
    public Monster activeMonster;
    public Monster selectedUserMonster;

    private int count = 0;
    public String back;


    @Inject
    public ActionFieldChangeMonsterController() {
    }

    public void setMonster(Monster monster) {
        activeMonster = monster;
    }

    @Override
    public Parent render() {
        Parent parent = super.render();

        back = translateString("back");

        textContent.setText(translateString("chooseMon"));

        userMonstersList = monsterService.getTeam().blockingFirst();

        showOptions();

        return parent;
    }

    public void showOptions() {
        count = 0;
        addActionOption(back, true);

        activeMonster = encounterStorage.getSession().getMonster(EncounterSlot.PARTY_FIRST);

        if (userMonstersList != null && !userMonstersList.isEmpty()) {
            for (Monster monster : userMonstersList) {
                if (monster.currentAttributes().health() > 0 && (activeMonster == null || !activeMonster._id().equals(monster._id()))) {
                    subscribe(presetService.getMonster(monster.type()), type -> {
                        selectedUserMonster = monster;
                        addActionOption(monster._id() + " " + type.name(), false);
                    });
                }
            }
        }
    }

    public void addActionOption(String option, boolean isBackOption) {
        // id is in 0 and name in 1
        String[] idAndName = option.split(" ");

        HBox optionContainer;
        if (option.equals(back)) {
            optionContainer = actionFieldControllerProvider.get().getOptionContainer(option);
            optionContainer.setOnMouseClicked(event -> openAction(option));
        } else {
            optionContainer = actionFieldControllerProvider.get().getOptionContainer(idAndName[1]);
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
            actionFieldControllerProvider.get().openMainMenu();
        } else {
            if (activeMonster == null || activeMonster.currentAttributes().health() == 0) {
                subscribe(encounterService.changeDeadMonster(selectedUserMonster),
                        opponent -> {});
            } else {
                subscribe(encounterService.makeChangeMonsterMove(selectedUserMonster),
                        next -> {});
            }

            actionFieldControllerProvider.get().openBattleLog();
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
