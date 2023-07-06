package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ActionFieldChangeMonsterController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public Text textContent;
    @FXML
    public ListView<HBox> changeMonListView;

    @Inject
    MonsterService monsterService;
    @Inject
    PresetService presetService;

    @Inject
    Provider<ActionFieldMainMenuController> actionFieldMainMenuController;

    public List<Monster> userMonstersList;
    public Monster activeMonster;
    public String selectedUserMonster;


    @Inject
    public ActionFieldChangeMonsterController() {
    }
    public void setMonster(Monster monster) {
        activeMonster = monster;
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        textContent.setText("Choose Mon:");

        if(monsterService.getTeam() != null) {
            userMonstersList = monsterService.getTeam().blockingFirst();
        }

        setAction();

        return parent;
    }

    public void setAction() {
        if(userMonstersList != null && !userMonstersList.isEmpty()) {
            for (Monster monster : userMonstersList) {
                subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> {
                    selectedUserMonster = type.name();
                    addActionOption(type.name(), false);
                });
            }
        }

        addActionOption("Back", true);
    }

    public void addActionOption(String optionText, boolean isBackOption) {
        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        HBox optionContainer = new HBox(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openAction(optionText));

        int index = changeMonListView.getItems().size();
        optionLabel.setId("user_monster_label_" + index);

        if (isBackOption) {
            changeMonListView.getItems().add(optionContainer);
        } else {
            changeMonListView.getItems().add(changeMonListView.getItems().size() - 1, optionContainer);
        }
    }

    public void openAction(String option) {
        if (option.equals("Back")) {
            pane.getChildren().add(actionFieldMainMenuController.get().render());
        } else {
            selectedUserMonster = option;
            openAbilities(option);
        }
    }

    private void openAbilities(String name) {
        for (Monster monster : userMonstersList) {
            if (selectedUserMonster.equals(name)) {
                setMonster(monster);
            }
        }
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
