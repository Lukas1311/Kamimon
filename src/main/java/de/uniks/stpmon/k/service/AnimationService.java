package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class AnimationService {

    @Inject
    public AnimationService(){

    }

    @Inject
    Provider<HybridController> hybridControllerProvider;

    public Transition createTrainerEncounterAnimation(Circle blackPoint) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(3), blackPoint);
        scaleTransition.setToX(40);
        scaleTransition.setToY(40);


        ParallelTransition parallelTransition = new ParallelTransition(hybridControllerProvider.get().removeSidebarTransition(), scaleTransition);
        parallelTransition.play();
        return parallelTransition;
    }
}
