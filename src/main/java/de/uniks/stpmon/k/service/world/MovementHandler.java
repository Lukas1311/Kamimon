package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Optional;

public class MovementHandler {


    @Inject
    protected EventListener listener;
    @Inject
    protected WorldLoader worldLoader;
    protected TrainerAreaCache trainerCache;
    @Inject
    protected CacheManager cacheManager;
    // not injected, provider by parent user
    protected TrainerProvider trainerProvider;
    protected String trainerId;
    protected String eventName;

    @Inject
    public MovementHandler() {
    }

    public void setInitialTrainer(TrainerProvider trainerProvider) {
        if (trainerProvider == null) {
            throw new IllegalArgumentException("Trainer provider cannot be null");
        }
        Trainer trainer = trainerProvider.getTrainer();
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        this.trainerProvider = trainerProvider;
        this.eventName = String.format("areas.%s.trainers.%s.moved",
                trainer.area(),
                trainer._id());
        this.trainerId = trainer._id();
        trainerCache = cacheManager.trainerAreaCache();
    }

    public Observable<Trainer> onMovements() {
        if (trainerProvider == null) {
            return Observable.error(new IllegalStateException("Trainer provider not set"));
        }
        return trainerCache
                .listenValue(trainerId)
                // Skip initial value
                .skip(1)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(this::onMoveReceived);
    }

    protected Observable<Trainer> onMoveReceived(Trainer newTrainer) {
        return Observable.just(newTrainer);
    }

}
