package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

import javax.inject.Inject;



public class LoadingWildEncounterController extends Controller {

    private static final double START_ANGLE = 90;
    private static final double ANIMATION_DURATION = 10.0; // in Sekunden
    private static final double ANGLE_PER_SECOND = 360.0 / ANIMATION_DURATION;

    @FXML
    StackPane fullBox;

    @FXML
    Arc arc;


    @Inject
    public LoadingWildEncounterController() {

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public Parent render() {

        final Parent parent = super.render();

        fullBox.setAlignment(Pos.CENTER);
        StackPane.setAlignment(arc, Pos.CENTER);

        arc.setVisible(true);

        arc.setStartAngle(START_ANGLE);
        arc.setLength(0);
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.BLUE);
        arc.setStroke(Color.BLACK);

        // Erstelle eine Timeline für die Animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, e -> {
                    // Anfangszustand
                    arc.setLength(0);
                }),
                new KeyFrame(Duration.seconds(ANIMATION_DURATION), e -> {
                    // Endzustand
                    arc.setLength(360);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.setOnFinished(event -> {
            ImageView vsWildBackground = new ImageView();
            loadImage(vsWildBackground, "encounter/trainerEncounter0.png");
            vsWildBackground.setFitHeight(800);
            vsWildBackground.setFitWidth(1200);

            fullBox.getChildren().add(vsWildBackground);
        });

        // Starte die Animation
        timeline.play();

        return parent;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
