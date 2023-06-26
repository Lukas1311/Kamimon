package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class OpponentMonsterStatusController extends Controller {
    @FXML
    public ImageView opponentMonsterStatusView;
    @FXML
    public ProgressBar opponentHpBar;
    @FXML
    public Text opponentMonsterName;
    @FXML
    public Text opponentMonsterLevel;


    @Inject
    public OpponentMonsterStatusController() {}

    @Override
    public void init() {
        loadImage(opponentMonsterStatusView, "encounter/opponentMonsterStatus.png");
    }

    @Override
    public Parent render() {
        return super.render();
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
