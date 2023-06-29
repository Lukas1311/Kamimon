package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

public class StatusController extends Controller {
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
    IResourceService resourceService;
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
        } else {
            parent = load("OpponentMonsterStatus");
            loadImage(monsterStatusView, "encounter/opponentMonsterStatus.png");

        }




        monsterLevel.setText("Lvl. " + monster.level().toString());

        return parent;
    }

    public void loadMonsterDto(String id) {
        final double SCALE = 4.0;

        disposables.add(presetService.getMonster(monster._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> {
                    monsterName.setText(monsterTypeDto.name());
                }));

//        disposables.add(resourceService.getMonsterImage(id)
//                .observeOn(FX_SCHEDULER)
//                .subscribe(imageUrl -> {
//                    // Scale and set the image
//                    Image image = ImageUtils.scaledImageFX(imageUrl, SCALE);
//                    monsterImage.setImage(image);
//                }));
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
