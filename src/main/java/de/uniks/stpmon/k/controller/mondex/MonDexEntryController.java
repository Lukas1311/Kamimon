package de.uniks.stpmon.k.controller.mondex;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.inject.Provider;
import java.util.Optional;

public class MonDexEntryController extends Controller {

    @FXML
    public ImageView monImage;
    @FXML
    public Label nameLabel;
    @FXML
    public Label typeLabel;

    private final MonDexController monDexController;
    private final MonsterTypeDto monster;
    private boolean isEncountered = false;

    public MonDexEntryController(MonDexController monDexController, MonsterTypeDto entry,
                                 Provider<TrainerService> trainerServiceProvider) {
        this.monDexController = monDexController;
        this.monster = entry;
        Optional<Trainer> trainer = trainerServiceProvider.get().onTrainer().blockingFirst();
        trainer.ifPresent(value -> this.isEncountered = value.encounteredMonsterTypes().contains(entry.id()));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        typeLabel.setId("type" + monster.id() + "Label");

        monDexController.setMonDexImage(monster, isEncountered, monImage);

        String name = isEncountered ? monster.name() : "???";
        nameLabel.setText(name);

        typeLabel.setText("#" + monster.id());

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "mondex/";
    }
}
