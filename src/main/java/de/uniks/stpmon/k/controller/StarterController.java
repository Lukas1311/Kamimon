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
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StarterController extends ToastedController {

    @FXML
    public ImageView starterImage;
    @FXML
    public Label monsterNameLabel;
    @FXML
    public ImageView starterBackground;
    @FXML
    public StackPane starterPane;
    @FXML
    public Text descriptionText;

    @Inject
    IResourceService resourceService;
    @Inject
    PresetService presetService;

    @Inject
    public StarterController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(starterBackground, "starter-choice-box.png");
        return parent;
    }

    public void loadMonsterImage(String id) {
        subscribe(resourceService.getMonsterImage(id), imageUrl -> {
            Image image = ImageUtils.scaledImageFX(imageUrl, 4);
            starterImage.setImage(image);
        }, this::handleError);
    }

    public void loadMonsterName(String id) {
        subscribe(presetService.getMonster(id), monsterTypeDto -> {
            monsterNameLabel.setText(monsterTypeDto.name());
            descriptionText.setText(monsterTypeDto.description());
        }, this::handleError);
    }

    public void setStarter(String id) {
        loadMonsterName(id);
        loadMonsterImage(id);
    }

}
