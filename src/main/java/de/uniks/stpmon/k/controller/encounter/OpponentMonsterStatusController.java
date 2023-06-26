package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
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


    private final Monster monster;

    @Inject
    public OpponentMonsterStatusController(Monster monster) {
        this.monster = monster;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(opponentMonsterStatusView, "encounter/opponentMonsterStatus.png");

        opponentMonsterName.setText(monster._id());
        opponentMonsterLevel.setText("Lvl. " + monster.level().toString());

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
