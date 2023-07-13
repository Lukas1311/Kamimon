package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;

public class LoadingEncounterController extends Controller {

    @FXML
    StackPane fullBox;

    @FXML
    Circle blackPoint;

    @Inject
    Provider<EncounterOverviewController> encounterProvider;


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

        fullBox.setAlignment(Pos.CENTER);
        StackPane.setAlignment(blackPoint, Pos.CENTER);

        showEncounterAnimation();
        return parent;
    }

    public void showEncounterAnimation() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(3), blackPoint);
        scaleTransition.setToX(40);
        scaleTransition.setToY(40);

        scaleTransition.setOnFinished(event -> {
            ImageView vsBackground = new ImageView();
            loadImage(vsBackground, "encounter/trainerEncounter0.png");
            vsBackground.setFitHeight(800);
            vsBackground.setFitWidth(1200);

            fullBox.getChildren().add(vsBackground);

            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
            pauseTransition.setOnFinished(e -> {
                loadImage(vsBackground, "encounter/trainerEncounter1.png");

                PauseTransition pauseTransition1 = new PauseTransition(Duration.seconds(2));
                pauseTransition1.setOnFinished(f -> {
                    EncounterOverviewController controller = encounterProvider.get();
                    app.show(controller);
                });
                pauseTransition1.play();
            });

            pauseTransition.play();
        });

        scaleTransition.play();

    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
