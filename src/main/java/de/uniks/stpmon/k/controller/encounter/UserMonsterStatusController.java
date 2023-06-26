package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class UserMonsterStatusController extends Controller {
    @FXML
    public ImageView userMonsterStatusView;
    @FXML
    public ProgressBar userHpBar;
    @FXML
    public Text userMonsterName;
    @FXML
    public Text userMonsterLevel;
    @FXML
    public Text userMonsterHp;
    @FXML
    public ProgressBar userExperienceBar;


    @Inject
    public UserMonsterStatusController() {
    }

    @Override
    public void init() {
        loadImage(userMonsterStatusView, "encounter/userMonsterStatus.png");
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
