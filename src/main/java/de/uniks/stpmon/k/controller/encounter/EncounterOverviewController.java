package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.MonsterAttributes;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
    MonsterService monsterService;
    @Inject
    Provider<StatusController> statusControllerProvider;
    @Inject
    LoginController loginController;

    List<Monster> userMonstersList = new ArrayList<>();
    //    List<Monster> userMonstersList = monsterService.getTeam().blockingFirst();
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
        Monster zuendorn = new Monster(
                "109",
                "trainerStorage.getTrainer()._id()",
                0,
                2,
                2,
                null,
                new MonsterAttributes(
                        20,
                        20,
                        20,
                        20
                ),
                new MonsterAttributes(
                        12,
                        20,
                        20,
                        20
                )
        );
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
        userMonstersList.add(zuendorn);
        opponentMonstersList.add(angrian);
        opponentMonstersList.add(sanddorm);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");
        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        placeholder.setOnMouseClicked(e -> app.show(loginController));

        renderMonsters();
        animateMonsterEntrance();

        return parent;
    }

    private void renderMonsters() {
        for (int slot = 0; slot < userMonstersList.size(); slot++) {
            Monster monster = userMonstersList.get(slot);
            StatusController userStatusController = statusControllerProvider.get();
            userStatusController.setMonster(monster);
            userStatusController.loadMonsterDto(String.valueOf(monster._id()));
            userMonsters.getChildren().add(userStatusController.render());
            if (slot == 0) {
                loadMonsterImage(monster._id(), userMonster0, 1);
            } else if (slot == 1) {
                loadMonsterImage(monster._id(), userMonster1, 1);
                VBox.setMargin(userStatusController.fullBox, new Insets(-18, 0, 0, 125));
            }
        }
        for (int slot = 0; slot < opponentMonstersList.size(); slot++) {
            Monster monster = opponentMonstersList.get(slot);
            StatusController opponentStatusController = statusControllerProvider.get();
            opponentStatusController.setMonster(monster);
            opponentStatusController.loadMonsterDto(String.valueOf(monster._id()));
            opponentMonsters.getChildren().add(opponentStatusController.render());
            if (slot == 0) {
                loadMonsterImage(monster._id(), opponentMonster0, 0);
                VBox.setMargin(opponentStatusController.fullBox, new Insets(0, 125, 0, 0));
            } else if (slot == 1) {
                loadMonsterImage(monster._id(), opponentMonster1, 0);
                VBox.setMargin(opponentStatusController.fullBox, new Insets(-5, 0, 0, 0));
            }
        }
    }

    private void loadMonsterImage(String id, ImageView monsterImage, int orientation) {
        final double SCALE = 6.0;

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

    private void animateMonsterEntrance() {
        userMonsters.getChildren().get(1).setOpacity(0);
        opponentMonsters.getChildren().get(1).setOpacity(0);
        userMonster1.setOpacity(0);
        opponentMonster1.setOpacity(0);
        placeholder.setOpacity(0);

        TranslateTransition userTransition1 = new TranslateTransition(Duration.seconds(1), userMonsters.getChildren().get(0));
        userTransition1.setFromX(-400);
        userTransition1.setToX(0);

        TranslateTransition userMonsterTransition1 = new TranslateTransition(Duration.seconds(1), userMonster0);
        userMonsterTransition1.setFromX(-400);
        userMonsterTransition1.setToX(0);

        ParallelTransition userFullTransition1 = new ParallelTransition(userTransition1, userMonsterTransition1);

        TranslateTransition opponentTransition1 = new TranslateTransition(Duration.seconds(1), opponentMonsters.getChildren().get(0));
        opponentTransition1.setFromX(400);
        opponentTransition1.setToX(0);

        TranslateTransition opponentMonsterTransition1 = new TranslateTransition(Duration.seconds(1), opponentMonster0);
        opponentMonsterTransition1.setFromX(400);
        opponentMonsterTransition1.setToX(0);

        ParallelTransition opponentFullTransition1 = new ParallelTransition(opponentTransition1, opponentMonsterTransition1);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            userMonsters.getChildren().get(1).setOpacity(1);
            opponentMonsters.getChildren().get(1).setOpacity(1);
            userMonster1.setOpacity(1);
            opponentMonster1.setOpacity(1);
        });

        TranslateTransition userTransition2 = new TranslateTransition(Duration.seconds(1), userMonsters.getChildren().get(1));
        userTransition2.setFromX(-600);
        userTransition2.setToX(0);

        TranslateTransition userMonsterTransition2 = new TranslateTransition(Duration.seconds(1), userMonster1);
        userMonsterTransition2.setFromX(-600);
        userMonsterTransition2.setToX(0);

        ParallelTransition userFullTransition2 = new ParallelTransition(userTransition2, userMonsterTransition2);

        TranslateTransition opponentTransition2 = new TranslateTransition(Duration.seconds(1), opponentMonsters.getChildren().get(1));
        opponentTransition2.setFromX(600);
        opponentTransition2.setToX(0);

        TranslateTransition opponentMonsterTransition2 = new TranslateTransition(Duration.seconds(1), opponentMonster1);
        opponentMonsterTransition2.setFromX(600);
        opponentMonsterTransition2.setToX(0);

        ParallelTransition opponentFullTransition2 = new ParallelTransition(opponentTransition2, opponentMonsterTransition2);

        ParallelTransition parallel2 = new ParallelTransition(userFullTransition2, opponentFullTransition2);

        parallel2.setOnFinished(e -> {
            placeholder.setOpacity(1);
        });

        SequentialTransition sequence = new SequentialTransition(parallel1, parallel2);

        TranslateTransition actionFieldTransition = new TranslateTransition(Duration.seconds(1), placeholder);
        actionFieldTransition.setFromX(600);
        actionFieldTransition.setToX(0);

        SequentialTransition fullSequence = new SequentialTransition(sequence, actionFieldTransition);

        fullSequence.play();
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
