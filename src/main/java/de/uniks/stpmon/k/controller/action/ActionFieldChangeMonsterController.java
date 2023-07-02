package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
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
    public VBox changeMonBox;

    @Inject
    MonsterService monsterService;
    @Inject
    PresetService presetService;

    private final List<HBox> actionOptions = new ArrayList<>();
    private List<Monster> userMonstersList;


    @Inject
    public ActionFieldChangeMonsterController() {
        userMonstersList = List.of(
                MonsterBuilder.builder().setTrainer("trainerService.getMe()._id()").setId(102).setExperience(2).setLevel(3).create(),
                MonsterBuilder.builder().setTrainer("trainerService.getMe()._id()").setId(23).setExperience(2).setLevel(3).create());

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        textContent.setText("Choose Mon:");

        setAction();

        return parent;
    }

    public void loadMonsterName(String id) {

    }

    public void setAction() {
        for (Monster monster : userMonstersList) {
            /*
            subscribe(presetService.getMonster(String.valueOf(monster.type())), type -> {
                        addActionOption(type.name());
            });*/
            addActionOption(monster._id());
        }
        addActionOption("Back");
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
        optionContainer.setOnMouseClicked(event -> openAction(optionText));

        changeMonBox.getChildren().add(optionContainer);
    }

    public void openAction(String option) {
        switch (option) {
            case "Back" -> openMainMenu();
            default -> setActiveMonster(option);
        }
    }

    private void setActiveMonster(String monsterName) {
        // TODO set the active monster
    }

    private void openMainMenu()  {
        ActionFieldMainMenuController controller = new ActionFieldMainMenuController();
        pane.getChildren().add(controller.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
