package de.uniks.stpmon.k.controller.map;

import javax.inject.Inject;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import javafx.animation.PauseTransition;

import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportAnimation {

    @Inject
    CacheManager cacheMan;
    @Inject
    TrainerStorage trainerStorage;

    Trainer initialTrainerState;

    @Inject
    public TeleportAnimation() {
    }

    public void playFastTravelAnimation(Runnable callback) {
        TrainerCache trainerCache = cacheMan.trainerCache();
        initialTrainerState = trainerStorage.getTrainer();
        if (initialTrainerState == null) {
            return;
        }
        int initialDirection = initialTrainerState.direction();

        AtomicInteger initialPauseDelayMs = new AtomicInteger(1000);
        // use atomic so we can have it and modify in lambda call below
        AtomicInteger iteration = new AtomicInteger(1);
        int iterationEnd = 21;

        PauseTransition pause = new PauseTransition(Duration.millis(initialPauseDelayMs.get()));

        pause.setOnFinished(event -> {
            int newPauseDelayMs = initialPauseDelayMs.get();
            // new direction will be one of 0,1,2,3
            int newDirection = (initialDirection + iteration.get()) % 4;
            // after 3 surroundings 3 * 4 dirs = 12, the pause should be dropped to 200ms
            if (newDirection % 2 == 0) {
                newPauseDelayMs = initialPauseDelayMs.accumulateAndGet(300, (oldValue, x) -> Math.max((oldValue - x), 150));
            }
            Trainer updatedDirectionTrainer = TrainerBuilder.builder(trainerStorage.getTrainer())
                .setDirection(newDirection)
                .create();
            trainerCache.updateValue(updatedDirectionTrainer);
            iteration.incrementAndGet();

            // check if loop already ended
            if (iteration.get() <= iterationEnd) {
                // start transition one more time
                pause.setDuration(Duration.millis(newPauseDelayMs));
                pause.playFromStart();
            } else {
                trainerCache.updateValue(initialTrainerState);
                callback.run();
            }
        });
        // initial play to start animation
        pause.play();

    }
}
