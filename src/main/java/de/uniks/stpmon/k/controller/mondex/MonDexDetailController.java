package de.uniks.stpmon.k.controller.mondex;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.TrainerService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class MonDexDetailController extends Controller {

    @FXML
    public AnchorPane monDexDetailBox;
    @FXML
    public ImageView monImg;
    @FXML
    public Text name;
    @FXML
    public Text description;
    @FXML
    public HBox typeBox;

    @Inject
    TrainerService trainerService;
    @Inject
    MonDexController monDexController;

    @Inject
    public MonDexDetailController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(monDexDetailBox, getResourcePath() + "monDexBox.png");
        return parent;
    }

    public void loadMon(MonsterTypeDto mon) {
        Optional<Trainer> trainerOp = trainerService.onTrainer().blockingFirst();
        Trainer trainer;
        trainer = trainerOp.orElseGet(() -> trainerService.getMe());
        boolean isEncountered = trainer.encounteredMonsterTypes().contains(mon.id());

        monDexController.setMonDexImage(mon, isEncountered, monImg);

        //check if mon is encounterd
        if (isEncountered) {
            for (String type : mon.type()) {
                addTypeLabel(type);
            }
            name.setText(mon.name());
            description.setText(mon.description());
        } else {
            addTypeLabel("unknown");
            description.setText(translateString("not.seen.yet"));
        }
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
        return "mondex/";
    }
}
