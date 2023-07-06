package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    public ListView<HBox> chooseOpponentListView;

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

    @Inject
    public ActionFieldChooseOpponentController(){
        opponentMonstersList = List.of(
                MonsterBuilder.builder().setTrainer("opponent").setType(2).create(),
                MonsterBuilder.builder().setTrainer("opponent").setType(55).create());
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        userMonster = actionFieldChangeMonsterController.selectedUserMonster;
        textContent.setText("Which Mon should " + userMonster +" attack?");
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

    public void addActionOption(String optionText) {
        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        HBox optionContainer = new HBox(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> showBattleLog(optionText));

        int index = chooseOpponentListView.getItems().size();
        optionLabel.setId("opponent_monster_label_" + index);

        chooseOpponentListView.getItems().add(optionContainer);
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
