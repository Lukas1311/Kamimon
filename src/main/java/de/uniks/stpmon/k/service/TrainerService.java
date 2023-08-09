package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.service.storage.cache.TrainerCache;
import de.uniks.stpmon.k.utils.Direction;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
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
    private TrainerCache trainerCache;

    @Inject
    public TrainerService() {
    }

    public Trainer getMe() {
        return trainerStorage.getTrainer();
    }

    public Observable<Optional<Trainer>> onTrainer() {
        return cacheManager.trainerCache().listenValue(trainerStorage.getTrainer()._id());
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
        UpdateTrainerDto dto = new UpdateTrainerDto(trainername, null, null, null);
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
        UpdateTrainerDto dto = new UpdateTrainerDto(null, image, null, null);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    public Observable<Trainer> setTeam(List<String> team) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        UpdateTrainerDto dto = new UpdateTrainerDto(null, null, team, null);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    public Observable<Trainer> fastTravel(String area) {
        Trainer trainer = trainerStorage.getTrainer();
        if (trainer == null) {
            return Observable.empty();
        }
        UpdateTrainerDto dto = new UpdateTrainerDto(null, null, null, area);
        return regionApiService.updateTrainer(trainer.region(), trainer._id(), dto);
    }

    /**
     * Applies the change to the trainer cache only on the client side. This can be used to
     * update the trainer cache before the server responds or before the request is sent.
     *
     * @param monTeamList The new team
     */
    public void temporaryApplyTeam(List<String> monTeamList) {
        if (trainerCache == null) {
            trainerCache = cacheManager.trainerCache();
        }
        Trainer trainer = trainerStorage.getTrainer();
        Trainer newTrainer = TrainerBuilder.builder(trainer).addTeam(monTeamList).create();
        trainerCache.updateValue(newTrainer);
    }

    /**
     * Returns the trainer that is in the direction the trainer is facing.
     */
    public Optional<Trainer> getFacingTrainer(int distance) {
        // Return empty if distance is 0, this would be the player itself
        if (distance == 0) {
            return Optional.empty();
        }
        if (areaCache == null || areaCache.isDestroyed()) {
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
