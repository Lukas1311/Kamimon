package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;

public class LoadingEncounterController extends Controller {

    @FXML
    StackPane fullBox;
    @Inject
    Provider<EncounterOverviewController> encounterProvider;

    @Inject
    public LoadingEncounterController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        fullBox.setStyle("-fx-background-color: black");
        playVSAnimation();
        return parent;
    }

    private void playVSAnimation() {
        ImageView vsBackground0 = new ImageView();
        ImageView vsBackground1 = new ImageView();

        loadImage(vsBackground0, "encounter/trainerEncounter0.png");
        loadImage(vsBackground1, "encounter/trainerEncounter1.png");

        vsBackground0.setFitHeight(800);
        vsBackground1.setFitHeight(800);
        vsBackground0.setFitWidth(1200);
        vsBackground1.setFitWidth(1200);

        fullBox.getChildren().add(vsBackground0);
        fullBox.getChildren().add(vsBackground1);
        vsBackground1.setOpacity(0.0);


        FadeTransition fadeOutTransition0 = createFadeTransition(vsBackground0, 1.0, 0.0, 200);
        FadeTransition fadeInTransition0 = createFadeTransition(vsBackground0, 0.0, 1.0, 200);
        FadeTransition fadeOutTransition1 = createFadeTransition(vsBackground1, 1.0, 0.0, 200);
        FadeTransition fadeInTransition1 = createFadeTransition(vsBackground1, 0.0, 1.0, 200);

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(createParallelTransition(fadeOutTransition0, fadeInTransition1),
                createParallelTransition(fadeOutTransition1, fadeInTransition0));

        sequentialTransition.setCycleCount(5);

        sequentialTransition.setOnFinished(event -> vsBackground0.setOpacity(0.0));

        FadeTransition longFadeOut = createFadeTransition(vsBackground1, 1.0, 0.0, 500);

        SequentialTransition sequentialTransition1 = new SequentialTransition(sequentialTransition, longFadeOut);

        sequentialTransition1.setOnFinished(event -> {
            EncounterOverviewController controller = encounterProvider.get();
            app.show(controller);
        });

        sequentialTransition1.play();
    }

    private FadeTransition createFadeTransition(ImageView imageView, double from, double to, int millis) {
        FadeTransition f = new FadeTransition(Duration.millis(millis), imageView);
        f.setFromValue(from);
        f.setToValue(to);
        return f;
    }

    private ParallelTransition createParallelTransition(FadeTransition fadeTransition1, FadeTransition fadeTransition2) {
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition1, fadeTransition2);
        return parallelTransition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}