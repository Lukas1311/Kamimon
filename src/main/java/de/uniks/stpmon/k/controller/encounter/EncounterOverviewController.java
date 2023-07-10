package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
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
    IResourceService resourceService;
    @Inject
    MonsterService monsterService;
    @Inject
    Provider<StatusController> statusControllerProvider;
    @Inject
    LoginController loginController;

    @Inject
    LoadingEncounterController loadingEncounterController;

    public List<Monster> userMonstersList;
    public List<Monster> opponentMonstersList;

    @Inject
    public EncounterOverviewController() {
        opponentMonstersList = new ArrayList<>();
    }

    @Override
    public void init() {
        super.init();

        subscribe(monsterService.getTeam(), team -> userMonstersList.addAll(team));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        loadImage(background, "encounter/FOREST.png");
        background.fitHeightProperty().bind(fullBox.heightProperty());
        background.fitWidthProperty().bind(fullBox.widthProperty());

        placeholder.setOnMouseClicked(e -> app.show(loginController));

        renderMonsterLists();
        animateMonsterEntrance();
        animateEncounterStart();

        return parent;
    }

    private void animateEncounterStart() {
        loadingEncounterController.render();
    }

    private void renderMonsterLists() {
        renderMonsters(userMonstersList, userMonsters, userMonster0, userMonster1, true);
        renderMonsters(opponentMonstersList, opponentMonsters, opponentMonster0, opponentMonster1, false);
    }

    private void renderMonsters(List<Monster> monsterList, VBox monstersContainer, ImageView monsterImageView1, ImageView monsterImageView2, boolean isUser) {
        for (int slot = 0; slot < monsterList.size(); slot++) {
            Monster monster = monsterList.get(slot);
            StatusController statusController = statusControllerProvider.get();
            statusController.setMonster(monster);
            statusController.loadMonsterDto(String.valueOf(monster._id()));
            monstersContainer.getChildren().add(statusController.render());

            if (slot == 0) {
                loadMonsterImage(monster._id(), monsterImageView1, isUser ? 1 : 0);
                if (isUser) {
                    VBox.setMargin(statusController.fullBox, new Insets(-18, 0, 0, 125));
                } else {
                    VBox.setMargin(statusController.fullBox, new Insets(0, 125, 0, 0));
                }
            } else if (slot == 1) {
                loadMonsterImage(monster._id(), monsterImageView2, isUser ? 1 : 0);
                if (isUser) {
                    VBox.setMargin(statusController.fullBox, new Insets(-5, 0, 0, 0));
                }
            }
        }
    }

    public void loadMonsterImage(String monsterId, ImageView monsterImage, int orientation) {
        final double SCALE = 6.0;

        disposables.add(resourceService.getMonsterImage(monsterId)
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
        if (userMonstersList.size() > 1) {
            userMonsters.getChildren().get(1).setOpacity(0);
            userMonster1.setOpacity(0);
        }
        if (opponentMonstersList.size() > 1) {
            opponentMonsters.getChildren().get(1).setOpacity(0);
            opponentMonster1.setOpacity(0);
        }
        placeholder.setOpacity(0);

        //the first monster of the user and opponent always gets rendered
        TranslateTransition userTransition1 = new TranslateTransition(Duration.seconds(1), userMonsters.getChildren().get(0));
        userTransition1.setFromX(-600);
        userTransition1.setToX(0);

        TranslateTransition userMonsterTransition1 = new TranslateTransition(Duration.seconds(1), userMonster0);
        userMonsterTransition1.setFromX(-600);
        userMonsterTransition1.setToX(0);

        ParallelTransition userFullTransition1 = new ParallelTransition(userTransition1, userMonsterTransition1);

        TranslateTransition opponentTransition1 = new TranslateTransition(Duration.seconds(1), opponentMonsters.getChildren().get(0));
        opponentTransition1.setFromX(600);
        opponentTransition1.setToX(0);

        TranslateTransition opponentMonsterTransition1 = new TranslateTransition(Duration.seconds(1), opponentMonster0);
        opponentMonsterTransition1.setFromX(600);
        opponentMonsterTransition1.setToX(0);

        ParallelTransition opponentFullTransition1 = new ParallelTransition(opponentTransition1, opponentMonsterTransition1);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            if (userMonstersList.size() > 1) {
                userMonsters.getChildren().get(1).setOpacity(1);
                userMonster1.setOpacity(1);
            }
            if (opponentMonstersList.size() > 1) {
                opponentMonsters.getChildren().get(1).setOpacity(1);
                opponentMonster1.setOpacity(1);
            }
        });

        ParallelTransition userFullTransition2 = null;
        if (userMonstersList.size() > 1) {
            TranslateTransition userTransition2 = new TranslateTransition(Duration.seconds(1), userMonsters.getChildren().get(1));
            userTransition2.setFromX(-600);
            userTransition2.setToX(0);

            TranslateTransition userMonsterTransition2 = new TranslateTransition(Duration.seconds(1), userMonster1);
            userMonsterTransition2.setFromX(-600);
            userMonsterTransition2.setToX(0);

            userFullTransition2 = new ParallelTransition(userTransition2, userMonsterTransition2);
        }

        ParallelTransition opponentFullTransition2 = null;
        if (opponentMonstersList.size() > 1) {
            TranslateTransition opponentTransition2 = new TranslateTransition(Duration.seconds(1), opponentMonsters.getChildren().get(1));
            opponentTransition2.setFromX(600);
            opponentTransition2.setToX(0);

            TranslateTransition opponentMonsterTransition2 = new TranslateTransition(Duration.seconds(1), opponentMonster1);
            opponentMonsterTransition2.setFromX(600);
            opponentMonsterTransition2.setToX(0);

            opponentFullTransition2 = new ParallelTransition(opponentTransition2, opponentMonsterTransition2);
        }

        ParallelTransition parallel2 = new ParallelTransition();
        if (userFullTransition2 != null) {
            parallel2.getChildren().add(userFullTransition2);
        }
        if (opponentFullTransition2 != null) {
            parallel2.getChildren().add(opponentFullTransition2);
        }

        parallel2.setOnFinished(e -> placeholder.setOpacity(1));

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
