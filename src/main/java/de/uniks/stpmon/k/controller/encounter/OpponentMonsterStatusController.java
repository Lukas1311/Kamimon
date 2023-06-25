package de.uniks.stpmon.k.controller.encounter;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class OpponentMonsterStatusController {
    //TODO: remove Image form fxml and set it in the controller
    @FXML
    public ImageView opponentMonsterView;
    @FXML
    public ProgressBar opponentHpBar;
    @FXML
    public Text opponentMonsterName;
    @FXML
    public Text opponentMonsterLevel;



    @Inject
    public OpponentMonsterStatusController() {}


}
