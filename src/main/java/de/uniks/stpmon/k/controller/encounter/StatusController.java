package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

public class StatusController extends Controller {
    @FXML
    public VBox fullBox;
    @FXML
    public ImageView monsterStatusView;
    @FXML
    public ProgressBar hpBar;
    @FXML
    public Text monsterName;
    @FXML
    public Text monsterLevel;
    @FXML
    public Text monsterHp;
    @FXML
    public ProgressBar experienceBar;

    @Inject
    PresetService presetService;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    TrainerStorage trainerStorage;

    private Monster monster;

    @Inject
    public StatusController() {
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    @Override
    public Parent render() {
        final Parent parent;
        if (monster.trainer().equals("trainerStorage.getTrainer()._id()")) {
            parent = load("UserMonsterStatus");
            loadImage(monsterStatusView, "encounter/userMonsterStatus.png");
            loadMonsterInformation();
        } else {
            parent = load("OpponentMonsterStatus");
            loadImage(monsterStatusView, "encounter/opponentMonsterStatus.png");
        }

        double maxHp = monster.attributes().health();
        double currentHp = monster.currentAttributes().health();
        double hpProgress = currentHp / maxHp;

        monsterLevel.setText("Lvl. " + monster.level().toString());
        hpBar.setProgress(hpProgress);


        return parent;
    }

    private void loadMonsterInformation() {
        monsterHp.setText(monster.currentAttributes().health() + " / " + monster.attributes().health());

        double maxExp = monster.level() * 3 - (monster.level() - 1) * 3;
        double currentExp = monster.experience();
        double expProgress = currentExp / maxExp;
        experienceBar.setProgress(expProgress);


//        // used to get the monster information for the monster of the trainer in the active region
//        disposables.add(regionService.getMonster("regionStorage.getRegion()._id()", monster._id())
//                .observeOn(FX_SCHEDULER)
//                .subscribe(monster1 -> {
//                    monsterHp.setText(monster1.currentAttributes() + " / " + monster1.attributes().health());
//                    monsterLevel.setText("Lvl. " + monster1.level().toString());
//                    hpBar.setProgress(monster1.attributes().health() - monster1.currentAttributes().health());
//                }));
    }


    public void loadMonsterDto(String id) {
        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    monsterName.setText(monsterTypeDto.name());
                }));
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
