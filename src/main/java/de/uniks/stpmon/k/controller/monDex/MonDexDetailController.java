package de.uniks.stpmon.k.controller.monDex;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;

@Singleton
public class MonDexDetailController extends Controller {

    @FXML
    public AnchorPane monDexDetailBox;
    @FXML
    public ImageView monPic;
    @FXML
    public Label name;
    @FXML
    public Label type;
    @FXML
    public Label description;
    @FXML
    public HBox typeBox;

    @Inject
    ResourceService resourceService;
    @Inject
    MonsterService monsterService;
    @Inject
    TrainerService trainerService;

    private boolean isCatched = true;

    @Inject
    public MonDexDetailController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(monDexDetailBox, "inventoryBox.png");
        return parent;
    }

    public void loadMon(MonsterTypeDto mon) {
        isCatched = trainerService.getMe().encounteredMonsterTypes().contains(mon.id());
        if (isCatched) {
            BufferedImage buff = resourceService.getMonsterImage(String.valueOf(mon.id())).blockingFirst();
            Image image = ImageUtils.scaledImageFX(buff, 1.0);
            monPic.setImage(image);

            for (String type : mon.type()) {
                addTypeLabel(type);
            }

            name.setText(mon.name());
            description.setText(mon.description());

        } else {
            //TODO; load black pic

            Label typeLabel = new Label();
            typeLabel.setText("???");
            typeBox.getChildren().add(typeLabel);

            description.setText(translateString("not.seen.yet"));
        }


    }

    private void addTypeLabel(String monsterType) {
        Label label = new Label();
        label.setId(monsterType.toUpperCase() + "_label");

        label.setText(monsterType.toUpperCase());
        label.getStyleClass().clear();
        label.getStyleClass().addAll("monster-type-general", "monster-type-" + monsterType);
        typeBox.getChildren().add(label);
    }

    private Image getImage(Integer typeId) {
        return null;
    }

    @Override
    public String getResourcePath() {
        return "monDex/";
    }
}
