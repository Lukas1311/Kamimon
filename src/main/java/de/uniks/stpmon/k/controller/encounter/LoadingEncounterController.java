package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.animation.*;
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
    Provider<HybridController> hybridControllerProvider;


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
        playVSAnimation();
        return parent;
    }



    private void playVSAnimation(){
        ImageView vsBackground0 = new ImageView();
        ImageView vsBackground1 = new ImageView();

        loadImage(vsBackground0, "encounter/trainerEncounter0.png");
        loadImage(vsBackground1, "encounter/trainerEncounter1.png");



        fullBox.getChildren().add(vsBackground0);
        fullBox.getChildren().add(vsBackground1);
        vsBackground1.setOpacity(0.0);

        FadeTransition fadeOutTransition0 = createFadeTransition(vsBackground0, 1.0, 0.0);
        FadeTransition fadeInTransition0 = createFadeTransition(vsBackground0, 0.0, 1.0);
        FadeTransition fadeOutTransition1 = createFadeTransition(vsBackground1, 1.0, 0.0);
        FadeTransition fadeInTransition1 = createFadeTransition(vsBackground1, 0.0, 1.0);

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(createParallelTransition(fadeOutTransition0, fadeInTransition1),
                createParallelTransition(fadeOutTransition1, fadeInTransition0));

        sequentialTransition.setCycleCount(5);

        sequentialTransition.setOnFinished(event -> {
            EncounterOverviewController controller = encounterProvider.get();
            app.show(controller);
        });

        sequentialTransition.play();

    }

    private FadeTransition createFadeTransition(ImageView imageView, double from, double to){
        FadeTransition f = new FadeTransition(Duration.millis(200), imageView);
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
