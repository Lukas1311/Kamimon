package de.uniks.stpmon.k.controller.map;

import javax.inject.Inject;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import javafx.animation.PauseTransition;

import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportAnimation extends Controller {

    @Inject
    TrainerCache trainerCache;
    @Inject
    TrainerStorage trainerStorage;

    Trainer initialTrainerState;

    @Inject
    public TeleportAnimation() {
    }

    public void playFastTravelAnimation() {
        initialTrainerState = trainerStorage.getTrainer();
        if (initialTrainerState == null) {
            return;
        }
        int initialDirection = initialTrainerState.direction();

        AtomicInteger initialPauseDelayMs = new AtomicInteger(2000);
        AtomicInteger delayReductionMs = new AtomicInteger(2);
        // use atomic so we can have it and modify in lambda call below
        AtomicInteger iteration = new AtomicInteger(1);
        int iterationEnd = 20;

        PauseTransition pause = new PauseTransition(Duration.millis(initialPauseDelayMs.get()));

        pause.setOnFinished(event -> {
            // new direction will be one of 0,1,2,3
            int newDirection = (initialDirection + iteration.get()) % 4;
            if (newDirection % 2 == 0) {
                int newDelay = delayReductionMs.accumulateAndGet(2, (oldValue, x) -> oldValue * x);
                // with mod 2 this should take about 22 times until the delay will be on zero
                initialPauseDelayMs.set(Math.max(initialPauseDelayMs.get() - newDelay, 0));
            }
            Trainer updatedDirectionTrainer = TrainerBuilder.builder(trainerStorage.getTrainer())
                .setDirection(newDirection)
                .create();
            trainerCache.updateValue(updatedDirectionTrainer);
            iteration.incrementAndGet();

            // check if loop already ended
            if (iteration.get() <= iterationEnd) {
                // start transition one more time
                pause.playFromStart();
            }
        });
        // initial play to start animation
        pause.play();
    }

    public void finishFastTravelAnimation() {
        trainerCache.updateValue(initialTrainerState);
    }

    @Override
    public String getResourcePath() {
        return "map/";
    }
}
