package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
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
    public static final int DISTANCE_CHECKED_FOR_TRAINERS = 2;

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
        return regionApiService.deleteTrainer(currentTrainer.region(), currentTrainer._id()).map(trainer->{
            trainerStorage.setTrainer(null);
            return trainer;
        });
    }

    public Observable<Trainer> setTrainerName(String trainername) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        Trainer newTrainer = new Trainer(trainer._id(),
                trainer.region(),
                trainer.user(),
                trainername,
                trainer.image(),
                trainer.coins(),
                trainer.area(),
                trainer.x(),
                trainer.y(),
                trainer.direction(),
                trainer.npc());
        trainerStorage.setTrainer(newTrainer);
        UpdateTrainerDto dto = new UpdateTrainerDto(trainername, null);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    public Observable<Trainer> setImage(String image) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        Trainer newTrainer = new Trainer(trainer._id(),
                trainer.region(),
                trainer.user(),
                trainer.name(),
                image,
                trainer.coins(),
                trainer.area(),
                trainer.x(),
                trainer.y(),
                trainer.direction(),
                trainer.npc());
        trainerStorage.setTrainer(newTrainer);
        UpdateTrainerDto dto = new UpdateTrainerDto(null, image);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    /**
     * Returns the trainer that is in the direction the trainer is facing.
     */
    public Optional<Trainer> getFacingTrainer() {
        if (areaCache == null || areaCache.getStatus() == ICache.Status.DESTROYED) {
            areaCache = cacheManager.trainerAreaCache();
        }
        Trainer trainer = trainerStorage.getTrainer();
        Direction dir = Direction.from(trainer);
        for (int i = 1; i <= DISTANCE_CHECKED_FOR_TRAINERS; i++) {
            int x = trainer.x() + dir.tileX() * i;
            int y = trainer.y() + dir.tileY() * i;
            // Upper bounds are not important because cache is designed for  up to 0xFFFF
            Optional<Trainer> trainerOptional = areaCache.getTrainerAt(x, y);
            if (trainerOptional.isPresent()) {
                return trainerOptional;
            }
        }
        return Optional.empty();
    }
}
