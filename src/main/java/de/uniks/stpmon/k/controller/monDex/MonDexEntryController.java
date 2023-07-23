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

    private final MonsterTypeDto monster;
    private final MonDexController monDexController;
    private BufferedImage bufferedImage;
    private final boolean isEncountered;

    @Inject
    public MonDexEntryController(MonDexController monDexController, Provider<ResourceService> resourceServiceProvider,
                                 MonsterTypeDto entry, Provider<TrainerService> trainerServiceProvider) {
        this.monster = entry;
        this.monDexController = monDexController;
        this.bufferedImage = resourceServiceProvider.get().getMonsterImage(String.valueOf(entry.id())).blockingFirst();

        this.isEncountered = trainerServiceProvider.get().getMe().encounteredMonsterTypes().contains(entry.id());
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        if (isEncountered) {
            nameLabel.setText(monster.name());

        } else {
            bufferedImage = ImageUtils.blackOutImage(bufferedImage);
            nameLabel.setText("???");
        }
        Image image = ImageUtils.scaledImageFX(bufferedImage, 1.0);
        monImage.setImage(image);
        typeLabel.setText("#" + monster.id());

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "monDex/";
    }
}
