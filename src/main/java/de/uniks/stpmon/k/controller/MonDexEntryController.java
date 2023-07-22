package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.ResourceService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.inject.Inject;

public class MonDexEntryController extends Controller {

    @FXML
    public ImageView monImage;
    @FXML
    public Label nameLabel;
    @FXML
    public Label typeLabel;

    @Inject
    ResourceService resourceService;
    @Inject
    MonsterService monService;

    final MonsterTypeDto monster;
    final MonDexController monDexController;

    @Inject
    public MonDexEntryController(MonDexController monDexController, MonsterTypeDto entry) {
        this.monster = entry;
        this.monDexController = monDexController;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();


        monImage.setImage(monDexController.getMonsterImage(monster.image()));
        nameLabel.setText(monster.name());

        typeLabel.setText(String.valueOf(monster.id()));


        return parent;
    }
}
