package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.PresetService;
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
    PresetService presetService;

    private final Monster monster;

    @Inject
    public UserMonsterStatusController(Monster monster) {
        this.monster = monster;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(userMonsterStatusView, "encounter/userMonsterStatus.png");

        userMonsterName.setText(monster._id());
        userMonsterLevel.setText("Lvl. " + monster.level().toString());

        disposables.add(presetService.getMonster(monster._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    userMonsterName.setText(monsterTypeDto.name());
                }));

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
