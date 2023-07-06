package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ActionFieldChooseOpponentController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public Text textContent;
    @FXML
    public HBox chooseOpponentBox;

    @Inject
    TrainerService trainerService;
    @Inject
    PresetService presetService;
    @Inject
    ActionFieldBattleLogController actionFieldBattleLogController;
    @Inject
    ActionFieldChangeMonsterController actionFieldChangeMonsterController;

    public List<Monster> opponentMonstersList;
    public String selectedOpponentMonster;
    public String userMonster;

    private int count = 0;

    @Inject
    public ActionFieldChooseOpponentController(){
        opponentMonstersList = List.of(
                MonsterBuilder.builder().setTrainer("opponent").setType(2).create(),
                MonsterBuilder.builder().setTrainer("opponent").setType(55).create(),
                MonsterBuilder.builder().setTrainer("opponent").setType(45).create());
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        userMonster = actionFieldChangeMonsterController.selectedUserMonster;
        textContent.setText(translateString("attackMon", userMonster));
        setAction();

        return parent;
    }

    public void setAction() {
        if(opponentMonstersList != null) {
            for (Monster monster : opponentMonstersList) {
                subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> addActionOption(type.name()));
            }
        }
    }

    public void addActionOption(String option) {
        Text arrowText = new Text(" >");
        Text optionText = new Text(option);

        arrowText.setVisible(false);

        HBox optionContainer = new HBox(arrowText, optionText);

        optionContainer.setOnMouseEntered(event -> arrowText.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowText.setVisible(false));
        optionContainer.setOnMouseClicked(event -> showBattleLog(option));

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


    private void showBattleLog(String option) {
        selectedOpponentMonster = option;
        pane.getChildren().add(actionFieldBattleLogController.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
