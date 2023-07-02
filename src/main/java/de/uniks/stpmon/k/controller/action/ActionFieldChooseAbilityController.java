package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ActionFieldChooseAbilityController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public VBox chooseAbilityBox;

    @Inject
    PresetService presetService;
    private final List<HBox> actionOptions = new ArrayList<>();
    private Monster monster;

    @Inject
    public ActionFieldChooseAbilityController() {}

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        loadMonAbility();
        return parent;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public void loadMonAbility() {
        for (String key : monster.abilities().keySet()) {
            if (monster.abilities().containsKey(key)) {
                HBox abilityBox = addActionOption(key);
                chooseAbilityBox.getChildren().add(abilityBox);
            }
        }
    }

    public HBox addActionOption(String optionText) {
        HBox optionContainer = new HBox();
        actionOptions.add(optionContainer);

        Label arrowLabel = new Label("> ");
        Label optionLabel = new Label(optionText);

        arrowLabel.setVisible(false);

        optionContainer.getChildren().addAll(arrowLabel, optionLabel);

        optionContainer.setOnMouseEntered(event -> arrowLabel.setVisible(true));
        optionContainer.setOnMouseExited(event -> arrowLabel.setVisible(false));
        optionContainer.setOnMouseClicked(event -> openOption(monster));

        chooseAbilityBox.getChildren().add(optionContainer);
        disposables.add(presetService.getAbility(monster._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(ability -> {
                    optionLabel.setText(ability.name());
                }));

        return optionContainer;
    }

    private void openOption(Monster monster) {
        ActionFieldBattleLogController controller = new ActionFieldBattleLogController();
        pane.getChildren().add(controller.render());
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }

}
