package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.ScaleTransition;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.inject.Inject;
import java.util.ArrayList;

public class LoadingEncounterController extends Controller {

    @FXML
    StackPane fullBox;

    @FXML
    Circle blackPoint;


    @Inject
    public LoadingEncounterController() {

    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // Positioniert den Punkt in der Mitte des blackPoint Panes

        // Erstellt die ScaleTransition für die Animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(3), blackPoint);
        scaleTransition.setToX(30); // Vergrößert den Punkt auf das Dreifache
        scaleTransition.setToY(30);
        //scaleTransition.setCycleCount(ScaleTransition.INDEFINITE); // Wiederholt die Animation unendlich oft
        //scaleTransition.setAutoReverse(true); // Führt die Animation in umgekehrter Richtung aus
        // Startet die Animation

        // Setzt die Aktionen nach der Animation
        scaleTransition.setOnFinished(event -> {
            // Erstelle die ImageView
            ImageView vsBackground = new ImageView();
            loadImage(vsBackground, "encounter/trainerEncounter0.png");

            // Füge die ImageView zur StackPane hinzu
            fullBox.getChildren().add(vsBackground);

            // Füge eine Pause von 3 Sekunden hinzu
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
            pauseTransition.setOnFinished(e -> {
                // Ändere das Bild nach 3 Sekunden
                loadImage(vsBackground, "encounter/trainerEncounter1.png");
            });

            pauseTransition.play();
        });

        scaleTransition.play();
        return parent;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
