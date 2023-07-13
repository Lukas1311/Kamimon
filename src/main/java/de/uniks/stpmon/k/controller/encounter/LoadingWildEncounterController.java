package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;


public class LoadingWildEncounterController extends Controller {

    @FXML
    Pane fullBox;



    private final List<Rectangle> rectangles = new ArrayList<>();;

    @Inject
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;


    @Inject
    public LoadingWildEncounterController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        creatRectangles();
        ParallelTransition parallelTransition = setUpTransition();
        FadeTransition wildTransition = getWildTransition();
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition,pauseTransition, wildTransition);
        sequentialTransition.setOnFinished(event -> {
            EncounterOverviewController controller = encounterOverviewControllerProvider.get();
            app.show(controller);
        });
        sequentialTransition.play();



        System.out.println("Done");

        return parent;
    }

    private void creatRectangles(){
        for(int i = 0; i < 4; i++){
            Rectangle rectangle = new Rectangle(1280, 180);
            if(i % 2 == 0){
                rectangle.setTranslateX(1280);
            }else{
                rectangle.setTranslateX(-1280);
            }
            rectangle.setY(i * 180);
            fullBox.getChildren().add(rectangle);
            rectangles.add(rectangle);
        }
    }

    private ParallelTransition setUpTransition(){
        ParallelTransition parallelTransition = new ParallelTransition();
        for(Rectangle r : rectangles){
            TranslateTransition transition = new TranslateTransition(Duration.seconds(1), r);
            transition.setToX(0);
            parallelTransition.getChildren().add(transition);
        }
        return parallelTransition;
    }

    private FadeTransition getWildTransition(){
        ImageView vsWildBackground = new ImageView();
        loadImage(vsWildBackground, "encounter/wildEncounter.png");
        vsWildBackground.setFitHeight(720);
        vsWildBackground.setFitWidth(1280);
        fullBox.getChildren().add(vsWildBackground);
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
