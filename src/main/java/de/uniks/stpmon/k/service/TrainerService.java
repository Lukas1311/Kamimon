package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerService {

    @Inject
    RegionApiService regionApiService;
    @Inject
    TrainerStorage trainerStorage;

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
        return regionApiService.deleteTrainer(currentTrainer._id());
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
        return regionApiService.updateTrainer(trainer._id(), trainer.region(), dto);
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
        UpdateTrainerDto dto = new UpdateTrainerDto(null,image);
        return regionApiService.updateTrainer(trainer._id(), trainer.region(), dto);
    }
}
