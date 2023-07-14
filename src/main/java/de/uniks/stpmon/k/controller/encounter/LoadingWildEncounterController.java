package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;


public class LoadingWildEncounterController extends Controller {

    @FXML
    BorderPane fullBox;

    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;


    @Inject
    public LoadingWildEncounterController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        FadeTransition wildTransition = getWildTransition();

        wildTransition.setOnFinished(event -> {
            EncounterOverviewController controller = encounterOverviewControllerProvider.get();
            app.show(controller);
        });

        wildTransition.play();

        return parent;
    }

    private FadeTransition getWildTransition(){
        ImageView vsWildBackground = new ImageView();
        loadImage(vsWildBackground, "encounter/wild.png");
        vsWildBackground.setFitHeight(300);
        vsWildBackground.setOpacity(0.0);
        fullBox.setCenter(vsWildBackground);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), vsWildBackground);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        return fadeTransition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}
