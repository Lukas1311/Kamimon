package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class ActionFieldBattleLogController extends Controller {
    @FXML
    public StackPane pane;
    public ImageView background;
    public Text trainerAttackText;
    public Text opponentAttackText;

    @Inject
    public ActionFieldBattleLogController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "action_menu_background.png");
        return parent;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
