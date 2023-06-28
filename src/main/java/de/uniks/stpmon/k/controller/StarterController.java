package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StarterController extends Controller {
    @FXML
    public ImageView starterImage;
    @FXML
    public Label monsterNameLabel;
    @FXML
    public ImageView starterBackground;
    @FXML
    public StackPane starterPane;

    @Inject
    IResourceService resourceService;
    @Inject
    PresetService presetService;

    @Inject
    public StarterController() {}

    @Override
    public Parent render() {
        //loadImage(starterBackground, "starter-choice-box.png");
        return super.render();
    }

    public void loadMonsterImage(String id) {
        disposables.add(resourceService.getMonsterImage(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(imageUrl -> {
                    Image image = ImageUtils.scaledImageFX(imageUrl, 4);
                    starterImage.setImage(image);
                }));
    }

    public void loadMonsterName(String id) {
        disposables.add(presetService.getMonster(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(monsterTypeDto -> monsterNameLabel.setText(monsterTypeDto.name())));
    }

    public void setStarter(String id) {
        loadMonsterName(id);
        loadMonsterImage(id);
    }
}
