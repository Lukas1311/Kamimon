package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.image.BufferedImage;

public class MonDexEntryController extends Controller {

    @FXML
    public ImageView monImage;
    @FXML
    public Label nameLabel;
    @FXML
    public Label typeLabel;

    @Inject
    MonsterService monService;

    final MonsterTypeDto monster;
    final MonDexController monDexController;
    final Image monsterImage;

    @Inject
    public MonDexEntryController(MonDexController monDexController, Provider<ResourceService> resourceServiceProvider, MonsterTypeDto entry) {
        this.monster = entry;
        this.monDexController = monDexController;
        BufferedImage buff = resourceServiceProvider.get().getMonsterImage(String.valueOf(entry.id())).blockingFirst();
        this.monsterImage = ImageUtils.scaledImageFX(buff, 1.0);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();


        monImage.setImage(monsterImage);
        nameLabel.setText(monster.name());

        typeLabel.setText(String.valueOf(monster.id()));


        return parent;
    }
}
