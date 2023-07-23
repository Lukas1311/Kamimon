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
import javafx.scene.text.Text;

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
    public Text name;
    @FXML
    public Text type;
    @FXML
    public Text description;
    @FXML
    public HBox typeBox;

    @Inject
    ResourceService resourceService;
    @Inject
    MonsterService monsterService;
    @Inject
    TrainerService trainerService;

    private boolean isEncountered = false;


    @Inject
    public MonDexDetailController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(monDexDetailBox, "monDexBox.png");
        return parent;
    }

    public void loadMon(MonsterTypeDto mon) {
        isEncountered = trainerService.getMe().encounteredMonsterTypes().contains(mon.id());
        BufferedImage buff = resourceService.getMonsterImage(String.valueOf(mon.id())).blockingFirst();
        //check if mon is encounterd
        if (isEncountered) {

            for (String type : mon.type()) {
                addTypeLabel(type);
                addTypeLabel(type);
            }

            name.setText(mon.name());
            description.setText(mon.description());
            //description.setText("Wingardiummaven is a dark raven and flies through the skies of Albertania with stealth and cunning. Its sleek black feathers conceal it from prying eyes, and its piercing gaze strikes fear into the hearts of those who dare to challenge it.");

        } else {
            //buff = ImageUtils.blackOutImage(buff);

            addTypeLabel("unknown");


            description.setText(translateString("not.seen.yet"));
            //description.setText("Obsidianwing, the colossal dark crow, casts a shadow of foreboding as it soars through the skies. Its obsidian feathers gleam with an eerie luster, and its piercing gaze instills fear in the hearts of all who witness its flight.");
        }
        Image image = ImageUtils.scaledImageFX(buff, 1.0);
        monPic.setImage(image);

    }

    private void addTypeLabel(String monsterType) {
        Label label = new Label();
        label.setId(monsterType.toUpperCase() + "_label");

        if (monsterType.equals("unknown")) {
            label.setText("???");
        } else {
            label.setText(monsterType.toUpperCase());
        }
        label.getStyleClass().clear();
        label.getStyleClass().addAll("monster-type-general",
                "monster-type-" + monsterType,
                "monster-information-font");
        typeBox.getChildren().add(label);
    }

    @Override
    public String getResourcePath() {
        return "monDex/";
    }
}
