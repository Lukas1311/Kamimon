package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.utils.ImageUtils;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;

public class EncounterOverviewController extends Controller {

    public static final double IMAGE_SCALE = 6.0;
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
    Provider<StatusController> statusControllerProvider;
    @Inject
    LoginController loginController;
    @Inject
    SessionService sessionService;

    @Inject
    public EncounterOverviewController() {
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

        return parent;
    }

    private void renderMonsterLists() {
        for (EncounterMember member : sessionService.getMembers()) {
            if (member.attacker()) {
                if (member.index() == 0) {
                    renderMonsters(opponentMonsters, opponentMonster0, member);
                } else {
                    renderMonsters(opponentMonsters, opponentMonster1, member);
                }
            } else {
                if (member.index() == 0) {
                    renderMonsters(userMonsters, userMonster0, member);
                } else {
                    renderMonsters(userMonsters, userMonster1, member);
                }
            }
        }
    }

    private void renderMonsters(VBox monstersContainer, ImageView monsterImageView, EncounterMember member) {
        if (!sessionService.hasMember(member)) {
            return;
        }
        Monster monster = sessionService.getMonster(member);
        StatusController statusController = statusControllerProvider.get();
        statusController.setMember(member);
        monstersContainer.getChildren().add(statusController.render());

        if (member.index() == 0) {
            loadMonsterImage(String.valueOf(monster.type()), monsterImageView, member.attacker());
            if (!member.attacker()) {
                VBox.setMargin(statusController.fullBox, new Insets(-18, 0, 0, 125));
            } else {
                VBox.setMargin(statusController.fullBox, new Insets(0, 125, 0, 0));
            }
        } else if (member.index() == 1) {
            loadMonsterImage(String.valueOf(monster.type()), monsterImageView, member.attacker());
            if (!member.attacker()) {
                VBox.setMargin(statusController.fullBox, new Insets(-5, 0, 0, 0));
            }
        }
    }

    public void loadMonsterImage(String monsterId, ImageView monsterImage, boolean attacker) {
        subscribe(resourceService.getMonsterImage(monsterId), imageUrl -> {
            // Scale and set the image
            Image image = ImageUtils.scaledImageFX(imageUrl, IMAGE_SCALE);
            if (attacker) {
                monsterImage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            } else {
                monsterImage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
            monsterImage.setImage(image);
        });
    }

    private void animateMonsterEntrance() {
        ObservableList<Node> teamMonsters = userMonsters.getChildren();
        if (teamMonsters.size() > 1) {
            teamMonsters.get(1).setOpacity(0);
            userMonster1.setOpacity(0);
        }
        ObservableList<Node> attackerMonsters = opponentMonsters.getChildren();
        if (attackerMonsters.size() > 1) {
            attackerMonsters.get(1).setOpacity(0);
            opponentMonster1.setOpacity(0);
        }
        placeholder.setOpacity(0);

        //the first monster of the user and opponent always gets rendered
        ParallelTransition userFullTransition1 = createMonsterTransition(userMonster0, teamMonsters.get(0), false);
        ParallelTransition opponentFullTransition1 = createMonsterTransition(opponentMonster0, attackerMonsters.get(0), true);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            if (teamMonsters.size() > 1) {
                teamMonsters.get(1).setOpacity(1);
                userMonster1.setOpacity(1);
            }
            if (attackerMonsters.size() > 1) {
                attackerMonsters.get(1).setOpacity(1);
                opponentMonster1.setOpacity(1);
            }
        });

        ParallelTransition userFullTransition2 = null;
        if (teamMonsters.size() > 1) {
            userFullTransition2 = createMonsterTransition(userMonster1, teamMonsters.get(1), false);
        }

        ParallelTransition opponentFullTransition2 = null;
        if (attackerMonsters.size() > 1) {
            opponentFullTransition2 = createMonsterTransition(opponentMonster1, attackerMonsters.get(1), true);
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

        SequentialTransition fullSequence = new SequentialTransition(sequence, createNodeTransition(placeholder, true));

        fullSequence.play();
    }

    private ParallelTransition createMonsterTransition(Node image, Node status, boolean attacker) {
        //the first monster of the user and opponent always gets rendered
        return new ParallelTransition(createNodeTransition(status, attacker), createNodeTransition(image, attacker));
    }


    private TranslateTransition createNodeTransition(Node node, boolean fromRight) {
        TranslateTransition monsterTransition = new TranslateTransition(Duration.seconds(1), node);
        monsterTransition.setFromX(fromRight ? 600 : -600);
        monsterTransition.setToX(0);
        return monsterTransition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
