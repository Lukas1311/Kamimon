package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.views.DexCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class MonDexController extends Controller {

    @FXML
    public AnchorPane monDexPane;
    @FXML
    public ListView<MonsterTypeDto> dexList;

    @Inject
    MonsterService monService;
    @Inject
    PresetService presetService;
    @Inject
    ResourceService resourceService;

    private final ObservableList<MonsterTypeDto> allMonsters = FXCollections.observableArrayList();


    @Inject
    public MonDexController() {

    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(monDexPane, "inventoryBox.png");

        subscribe(presetService.getMonsters(), (monList) -> {
            if (!monList.isEmpty()) {
                allMonsters.add(monList.get(0));
                allMonsters.add(monList.get(1));
                dexList.setCellFactory(param -> new DexCell(this));

                dexList.setItems(FXCollections.observableArrayList(allMonsters));
            }
        });


        return parent;
    }

    public Image getMonsterImage(String monsterImage) {
        BufferedImage buff = resourceService.getMonsterImage(String.valueOf(monsterImage)).blockingFirst();
        return ImageUtils.scaledImageFX(buff, 1.0);

    }
}
