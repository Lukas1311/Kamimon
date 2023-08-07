package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.stpmon.k.controller.StarterController.StarterOption.ITEM;
import static de.uniks.stpmon.k.controller.StarterController.StarterOption.MON;

@Singleton
public class StarterController extends ToastedController {

    @FXML
    public ImageView starterImage;
    @FXML
    public Label monsterNameLabel;
    @FXML
    public StackPane starterPane;
    @FXML
    public Text descriptionText;
    @FXML
    public VBox textBox;
    @FXML
    public TextFlow textFlow;

    @Inject
    IResourceService resourceService;
    @Inject
    PresetService presetService;

    @Inject
    public StarterController() {
    }

    public enum StarterOption {
        ITEM("item"),
        MON("mon");

        private final String entryText;

        StarterOption(final String entryText) {
            this.entryText = entryText;
        }

        @Override
        public String toString() {
            return entryText;
        }
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(starterPane, "starter-choice-box.png");
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

    public void loadItemImage(String id) {
        subscribe(resourceService.getItemImage(id), imageUrl -> {
            starterPane.getChildren().remove(monsterNameLabel);
            Image image = ImageUtils.scaledImageFX(imageUrl, 4);
            starterImage.setImage(image);
        }, this::handleError);
    }

    public void loadItemName(String id) {
        subscribe(presetService.getItem(id), itemTypeDto -> {
            monsterNameLabel.setVisible(false);
            descriptionText.setText(translateString("get.item", itemTypeDto.name()));
            textFlow.setTextAlignment(TextAlignment.CENTER);
            textBox.setAlignment(Pos.BOTTOM_CENTER);
        }, this::handleError);
    }

    public void setStarter(String id, StarterOption option) {
        if (option.equals(MON)) {
            loadMonsterName(id);
            loadMonsterImage(id);
        } else if (option.equals(ITEM)) {
            loadItemImage(id);
            loadItemName(id);
        }
    }

}
