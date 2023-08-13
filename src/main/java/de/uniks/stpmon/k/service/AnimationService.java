package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class AnimationService {

    @Inject
    protected EffectContext effectContext;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    private ImageView ballView;
    private ImageView userMonster0;
    private ImageView userMonster1;
    private ImageView opponentMonster0;
    private ImageView opponentMonster1;
    private VBox actionFieldWrapperBox;


    @Inject
    public AnimationService() {
    }

    // ---------------- Encounter Animations ----------------
    public Transition createEncounterAnimation(Circle blackPoint) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(3), blackPoint);
        scaleTransition.setToX(55);
        scaleTransition.setToY(55);


        ParallelTransition parallelTransition = new ParallelTransition(hybridControllerProvider.get().removeSidebarTransition(), scaleTransition);
        parallelTransition.play();
        return parallelTransition;
    }

    public void setBallView(ImageView ballView) {
        this.ballView = ballView;
    }

    public void setUserMonster0(ImageView userMonster0) {
        this.userMonster0 = userMonster0;
    }

    public void setUserMonster1(ImageView userMonster1) {
        this.userMonster1 = userMonster1;
    }

    public void setOpponentMonster0(ImageView opponentMonster0) {
        this.opponentMonster0 = opponentMonster0;
    }

    public void setOpponentMonster1(ImageView opponentMonster1) {
        this.opponentMonster1 = opponentMonster1;
    }

    public void setActionFieldWrapperBox(VBox actionFieldWrapperBox) {
        this.actionFieldWrapperBox = actionFieldWrapperBox;
    }

    // ---------------- Encounter Animations - Monster Entrance ----------------
    public Transition playOpeningAnimation(StackPane fullBox, Pane blackPane) {
        if (fullBox.getChildren().contains(blackPane)) {
            return null;
        }
        blackPane.setPrefWidth(1280);
        blackPane.setPrefHeight(720);
        Rectangle rectangleTop = new Rectangle(1280, 360);
        rectangleTop.widthProperty().bind(fullBox.widthProperty());
        rectangleTop.heightProperty().bind(fullBox.heightProperty());
        Rectangle rectangleBottom = new Rectangle(1280, 360);
        rectangleBottom.widthProperty().bind(fullBox.widthProperty());
        rectangleBottom.heightProperty().bind(fullBox.heightProperty());
        rectangleBottom.setY(360);

        blackPane.getChildren().addAll(rectangleTop, rectangleBottom);

        fullBox.getChildren().add(blackPane);

        TranslateTransition rTopTransition = new TranslateTransition(Duration.seconds(1), rectangleTop);
        TranslateTransition rDownTransition = new TranslateTransition(Duration.seconds(1), rectangleBottom);

        rTopTransition.setToY(-800);
        rDownTransition.setToY(721);

        return new ParallelTransition(rTopTransition, rDownTransition);
    }

    public void animateMonsterEntrance(VBox userMonsters, VBox opponentMonsters, VBox wrappingVBox) {
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

        //the first monster of the user and opponent always gets rendered
        ParallelTransition userFullTransition1 =
                createMonsterTransition(userMonster0, teamMonsters.get(0), false);
        ParallelTransition opponentFullTransition1 =
                createMonsterTransition(opponentMonster0, attackerMonsters.get(0), true);

        ParallelTransition parallel1 = new ParallelTransition(userFullTransition1, opponentFullTransition1);

        parallel1.setOnFinished(e -> {
            wrappingVBox.setOpacity(1);
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
        SequentialTransition fullSequence = getSequentialTransition(parallel1, parallel2);

        fullSequence.play();
    }

    private SequentialTransition getSequentialTransition(ParallelTransition parallel1, ParallelTransition parallel2) {
        SequentialTransition sequence = new SequentialTransition(parallel1, parallel2);
        sequence.setOnFinished(e -> actionFieldWrapperBox.setOpacity(1));

        TranslateTransition actionFieldTransition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), actionFieldWrapperBox);
        actionFieldTransition.setFromX(600);
        actionFieldTransition.setToX(0);
        return new SequentialTransition(sequence, actionFieldTransition);
    }


    private TranslateTransition createNodeTransition(Node node, boolean fromRight) {
        TranslateTransition transition = new TranslateTransition(
                Duration.millis(effectContext.getEncounterAnimationSpeed()), node);
        transition.setFromX(fromRight ? 600 : -600);
        transition.setToX(0);
        return transition;
    }

    public ParallelTransition createMonsterTransition(Node image, Node status, boolean attacker) {
        //the first monster of the user and opponent always gets rendered
        return new ParallelTransition(createNodeTransition(status, attacker), createNodeTransition(image, attacker));
    }


    // ---------------- Encounter Animations - Catch Monster ----------------
    public ParallelTransition revertCatchAnimation() {
        FadeTransition ballFadeOut = new FadeTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() / 2), ballView);
        ballFadeOut.setFromValue(100);
        ballFadeOut.setToValue(0);

        FadeTransition monsterFadeIn = new FadeTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() / 2), opponentMonster0);
        monsterFadeIn.setFromValue(0);
        monsterFadeIn.setToValue(100);

        ParallelTransition parallelTransition = new ParallelTransition(ballFadeOut, monsterFadeIn);
        parallelTransition.play();
        parallelTransition.setOnFinished(e -> {
            //ballView.setOpacity(1.0f);
        });
        return parallelTransition;
    }

    public RotateTransition getBallRotation(int rotation) {
        RotateTransition rotateBallTransition = new RotateTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() / 4), ballView);
        if (rotation == 0) {
            rotateBallTransition.setByAngle(0); // Negative 45 degrees
            rotateBallTransition.setToAngle(45); // Positive 45 degrees
        } else if (rotation == 1) {
            rotateBallTransition.setByAngle(45);
            rotateBallTransition.setToAngle(-45); // Negative 45 degrees
        } else {
            rotateBallTransition.setByAngle(-45);
            rotateBallTransition.setToAngle(0); // Negative 45 degrees
        }
        return rotateBallTransition;
    }

    public SequentialTransition getBallFlight() {
        TranslateTransition translation =
                new TranslateTransition(Duration.millis(effectContext.getEncounterAnimationSpeed()), ballView);
        translation.setFromY(1000);
        translation.setFromX(-2000);
        translation.setToX(0);
        translation.setToY(0);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(effectContext.getEncounterAnimationSpeed() / 2), opponentMonster0);
        fadeTransition.setFromValue(100);
        fadeTransition.setToValue(0);
        return new SequentialTransition(translation, fadeTransition);
    }

}
