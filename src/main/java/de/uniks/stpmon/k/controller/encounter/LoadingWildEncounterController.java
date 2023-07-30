package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

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
        FadeTransition wildFadeInTransition = getWildTransition(true);
        FadeTransition wildFadeOutTransition = getWildTransition(false);

        SequentialTransition sequentialTransition = new SequentialTransition(wildFadeInTransition, wildFadeOutTransition);

        sequentialTransition.setOnFinished(event -> {
            EncounterOverviewController controller = encounterOverviewControllerProvider.get();
            app.show(controller);
        });

        sequentialTransition.play();

        return parent;
    }

    private FadeTransition getWildTransition(boolean isFadeIn){
        ImageView vsWildBackground = new ImageView();
        loadImage(vsWildBackground, "encounter/wild.png");
        vsWildBackground.setFitHeight(300);
        vsWildBackground.setOpacity(0.0);
        fullBox.setCenter(vsWildBackground);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), vsWildBackground);
        double fromValue = 1.0;
        double toValue = 0.0;
        if(isFadeIn){
            fromValue = 0.0;
            toValue = 1.0;
        }
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        return fadeTransition;
    }

    @Override
    public String getResourcePath() {
        return "encounter/";
    }
}