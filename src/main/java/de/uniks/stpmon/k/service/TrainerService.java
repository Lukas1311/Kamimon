package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class TrainerService {

    @Inject
    RegionApiService regionApiService;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    CacheManager cacheManager;
    // Current area cache, will be updated when the related area changes
    private TrainerAreaCache areaCache;

    @Inject
    public TrainerService() {
    }

    public Trainer getMe() {
        return trainerStorage.getTrainer();
    }

    public Observable<Trainer> deleteMe() {
        Trainer currentTrainer = trainerStorage.getTrainer();
        if (currentTrainer == null) {
            return Observable.empty();
        }
        return regionApiService.deleteTrainer(currentTrainer.region(), currentTrainer._id())
                .map(trainer -> {
                    trainerStorage.setTrainer(null);
                    return trainer;
                });
    }

    public Observable<Trainer> setTrainerName(String trainername) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        Trainer newTrainer = TrainerBuilder.builder(trainer)
                .setName(trainername)
                .create();
        trainerStorage.setTrainer(newTrainer);
        UpdateTrainerDto dto = new UpdateTrainerDto(trainername, null);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    public Observable<Trainer> setImage(String image) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        Trainer newTrainer = TrainerBuilder.builder(trainer)
                .setImage(image)
                .create();
        trainerStorage.setTrainer(newTrainer);
        UpdateTrainerDto dto = new UpdateTrainerDto(null, image);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    /**
     * Returns the trainer that is in the direction the trainer is facing.
     */
    public Optional<Trainer> getFacingTrainer(int distance) {
        // Return empty if distance is 0, this would be the player itself
        if (distance == 0) {
            return Optional.empty();
        }
        if (areaCache == null || areaCache.getStatus() == ICache.Status.DESTROYED) {
            areaCache = cacheManager.trainerAreaCache();
        }
        Trainer trainer = trainerStorage.getTrainer();
        Direction dir = Direction.from(trainer);
        int x = trainer.x() + dir.tileX() * distance;
        int y = trainer.y() + dir.tileY() * distance;
        // Upper bounds are not important because cache is designed for  up to 0xFFFF
        return areaCache.getTrainerAt(x, y);
    }
}
