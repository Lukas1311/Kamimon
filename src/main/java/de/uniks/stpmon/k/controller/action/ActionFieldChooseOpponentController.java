package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ActionFieldChooseOpponentController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public Text textContent;
    @FXML
    public VBox chooseOpponentBox;

    @Inject
    TrainerService trainerService;
    @Inject
    PresetService presetService;
    @Inject
    EncounterOverviewController encounterOverviewController;

    private final List<HBox> actionOptions = new ArrayList<>();
    public List<Monster> opponentMonstersList;

    @Inject
    public ActionFieldChooseOpponentController(){
        opponentMonstersList = List.of(
                MonsterBuilder.builder().setTrainer("opponent").setId(2).setExperience(2).setLevel(3).create(),
                MonsterBuilder.builder().setTrainer("opponent").setId(55).setExperience(2).setLevel(3).create());

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        textContent.setText("Which Mon should be attacked?");

        setAction();

        return parent;
    }

    public void setAction() {
        for (Monster monster : opponentMonstersList) {
            addActionOption(monster._id());
            /*
            subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> {
                addActionOption(type.name());
            });
             */
        }
    }

    public void addActionOption(String optionText) {
        HBox optionContainer = new HBox();
        actionOptions.add(optionContainer);

        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        optionContainer.getChildren().addAll(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> showBattleLog());

        chooseOpponentBox.getChildren().add(optionContainer);
    }

    private void showBattleLog() {
        ActionFieldBattleLogController controller = new ActionFieldBattleLogController();
        pane.getChildren().add(controller.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
