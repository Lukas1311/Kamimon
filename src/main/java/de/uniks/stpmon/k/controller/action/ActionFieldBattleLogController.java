package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionFieldBattleLogController extends Controller {
    @FXML
    public StackPane pane;
    @FXML
    public ImageView background;
    @FXML
    public Text trainerAttackText;
    @FXML
    public Text opponentAttackText;

    @Inject
    Provider<ActionFieldChangeMonsterController> actionFieldChangeMonsterController;
    @Inject
    Provider<ActionFieldChooseAbilityController> actionFieldChangeAbilityController;
    @Inject
    Provider<ActionFieldChooseOpponentController> actionFieldChooseOpponentController;

    @Inject
    public ActionFieldBattleLogController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");

        String userMonster = actionFieldChangeMonsterController.get().selectedUserMonster;
        String opponentMonster = actionFieldChooseOpponentController.get().selectedOpponentMonster;
        String userAbility = actionFieldChangeAbilityController.get().selectedAbility;
        trainerAttackText.setText(userMonster + " attacks " + opponentMonster + " with " + userAbility);
        opponentAttackText.setText("..." + " attacks " + "..." + " with " + "...");

        return parent;
    }
    @Override
    public String getResourcePath() {
        return "action/";
    }
}
