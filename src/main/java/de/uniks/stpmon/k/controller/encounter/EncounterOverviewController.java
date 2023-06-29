package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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
    public Rectangle placeholder;


    @Inject
    TrainerStorage trainerStorage;
    @Inject
    IResourceService resourceService;
    @Inject
    Provider<StatusController> statusControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;


    List<Monster> userMonstersList = new ArrayList<>();
    List<Monster> opponentMonstersList = new ArrayList<>();

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
                new MonsterAttributes(
                        20,
                        20,
                        20,
                        20
                ),
                new MonsterAttributes(
                        18,
                        20,
                        20,
                        20
                )
        );
//        Monster zuendorn = new Monster(
//                "109",
//                "trainerStorage.getTrainer()._id()",
//                0,
//                2,
//                2,
//                null,
//                new MonsterAttributes(
//                        20,
//                        20,
//                        20,
//                        20
//                ),
//                new MonsterAttributes(
//                        12,
//                        20,
//                        20,
//                        20
//                )
//        );
        Monster angrian = new Monster(
                "10",
                "opponentTrainer",
                0,
                3,
                3,
                null,
                new MonsterAttributes(
                        20,
                        20,
                        20,
                        20
                ),
                new MonsterAttributes(
                        4,
                        20,
                        20,
                        20
                )
        );
        Monster sanddorm = new Monster(
                "78",
                "opponentTrainer",
                0,
                4,
                4,
                null,
                new MonsterAttributes(
                        20,
                        20,
                        20,
                        20
                ),
                new MonsterAttributes(
                        14,
                        20,
                        20,
                        20
                )
        );
        userMonstersList.add(amogus);
////        userMonstersList.add(zuendorn);
//        opponentMonstersList.add(angrian);
        opponentMonstersList.add(sanddorm);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");
        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        placeholder.setOnMouseClicked(e -> hybridControllerProvider.get().openMain(MainWindow.INGAME));

        renderMonsters();

        return parent;
    }

    private void renderMonsters() {
        for (Monster monster : userMonstersList) {
            StatusController userStatusController = statusControllerProvider.get();
            userStatusController.setMonster(monster);
            userStatusController.loadMonsterDto(String.valueOf(monster._id()));
            userMonsters.getChildren().add(userStatusController.render());
            if (monster._id().equals(userMonstersList.get(0)._id())) {
                loadMonsterImage(String.valueOf(monster._id()), userMonster0, 1);
            } else if (monster._id().equals(userMonstersList.get(1)._id())) {
                loadMonsterImage(String.valueOf(monster._id()), userMonster1, 1);
                VBox.setMargin(userStatusController.fullBox, new Insets(-18, 0, 0, 125));
            }
        }
        for (Monster monster : opponentMonstersList) {
            StatusController opponentStatusController = statusControllerProvider.get();
            opponentStatusController.setMonster(monster);
            opponentStatusController.loadMonsterDto(String.valueOf(monster._id()));
            opponentMonsters.getChildren().add(opponentStatusController.render());
            if (monster._id().equals(opponentMonstersList.get(0)._id())) {
                loadMonsterImage(String.valueOf(monster._id()), opponentMonster0, 0);
                VBox.setMargin(opponentStatusController.fullBox, new Insets(0, 125, 0, 0));
            } else if (monster._id().equals(opponentMonstersList.get(1)._id())) {
                loadMonsterImage(String.valueOf(monster._id()), opponentMonster1,  0);
                VBox.setMargin(opponentStatusController.fullBox, new Insets(-5, 0, 0, 0));
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
