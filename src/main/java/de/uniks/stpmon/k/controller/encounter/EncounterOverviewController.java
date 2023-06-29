package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class EncounterOverviewController extends Controller {
    @FXML
    public StackPane fullBox;
    @FXML
    public ImageView background;
    @FXML
    public VBox userMonsters;
    @FXML
    public VBox opponentMonsters;
    @FXML
    public ImageView userMonster0;
    @FXML
    public ImageView userMonster1;
    @FXML
    public ImageView opponentMonster0;
    @FXML
    public ImageView opponentMonster1;



    @Inject
    TrainerStorage trainerStorage;
    @Inject
    IResourceService resourceService;
    @Inject
    Provider<StatusController> statusControllerProvider;


    List<Monster> encounterMonsters = new ArrayList<>();

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
        encounterMonsters.add(amogus);
        encounterMonsters.add(zuendorn);
        encounterMonsters.add(angrian);
        encounterMonsters.add(sanddorm);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");
        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        renderMonsters();

        return parent;
    }

    private void renderMonsters() {
        for (int i = 0; i < encounterMonsters.size(); i++) {
            Monster monster = encounterMonsters.get(i);
            if (monster.trainer().equals("trainerStorage.getTrainer()._id()")) {
                StatusController userStatusController = statusControllerProvider.get();
                userStatusController.setMonster(monster);
                userStatusController.loadMonsterDto(String.valueOf(monster._id()));
                if (i == 0) {
                    loadMonsterImage(String.valueOf(monster._id()), userMonster0, 1);
                } else if (i == 1) {
                    loadMonsterImage(String.valueOf(monster._id()), userMonster1, 1);
                }
                userMonsters.getChildren().add(userStatusController.render());
            } else {
                StatusController opponentStatusController = statusControllerProvider.get();
                opponentStatusController.setMonster(monster);
                opponentStatusController.loadMonsterDto(String.valueOf(monster._id()));
                if (i == 2) {
                    loadMonsterImage(String.valueOf(monster._id()), opponentMonster0, 0);
                } else if (i == 3) {
                    loadMonsterImage(String.valueOf(monster._id()), opponentMonster1,  0);
                }
                opponentMonsters.getChildren().add(opponentStatusController.render());
            }
        }
    }

    private void loadMonsterImage(String id, ImageView monsterImage, int orientation) {
        final double SCALE = 8.0;

        disposables.add(resourceService.getMonsterImage(id)
                .observeOn(FX_SCHEDULER)
                .subscribe(imageUrl -> {
                    // Scale and set the image
                    Image image = ImageUtils.scaledImageFX(imageUrl, SCALE);
                    if (orientation == 0) {
                        monsterImage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    } else {
                        monsterImage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                    }
                    monsterImage.setImage(image);
                }));
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
