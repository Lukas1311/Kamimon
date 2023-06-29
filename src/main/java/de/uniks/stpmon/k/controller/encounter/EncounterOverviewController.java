package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class EncounterOverviewController extends Controller {

    @FXML
    public ImageView background;
    @FXML
    public VBox userMonsters;
    @FXML
    public VBox opponentMonsters;


    @Inject
    TrainerStorage trainerStorage;

    @Inject
    Provider<StatusController> statusControllerProvider;

    List<Monster> dummyMonsters = new ArrayList<>();

    @Inject
    public EncounterOverviewController() {
    }

    @Override
    public void init() {
        Monster amogus = new Monster(
                "9",
                "trainerStorage.getTrainer()._id()",
                0,
                1,
                1,
                null,
                null,
                null
        );
        Monster zuendorn = new Monster(
                "109",
                "trainerStorage.getTrainer()._id()",
                0,
                2,
                2,
                null,
                null,
                null
        );
        Monster angrian = new Monster(
                "10",
                "opponentTrainer",
                0,
                3,
                3,
                null,
                null,
                null
        );
        Monster sanddorm = new Monster(
                "78",
                "opponentTrainer",
                0,
                4,
                4,
                null,
                null,
                null
        );
        dummyMonsters.add(amogus);
        dummyMonsters.add(zuendorn);
        dummyMonsters.add(angrian);
        dummyMonsters.add(sanddorm);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");

        renderMonsters();

        return parent;
    }

    private void renderMonsters() {
        for (Monster monster : dummyMonsters) {
            if (monster.trainer().equals("trainerStorage.getTrainer()._id()")) {
                StatusController userStatusController = statusControllerProvider.get();
                userStatusController.setMonster(monster);
                userStatusController.loadMonsterDto(String.valueOf(monster._id()));
                userMonsters.getChildren().add(userStatusController.render());
            } else {
                StatusController opponentStatusController = statusControllerProvider.get();
                opponentStatusController.setMonster(monster);
                opponentStatusController.loadMonsterDto(String.valueOf(monster._id()));
                opponentMonsters.getChildren().add(opponentStatusController.render());
            }
        }
    }



    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
