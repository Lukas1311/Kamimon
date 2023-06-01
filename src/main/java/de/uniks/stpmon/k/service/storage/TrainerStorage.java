package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerStorage {

    Trainer Trainer;

    @Inject
    public TrainerStorage() {
    }

    public Trainer getTrainer() {
        return Trainer;
    }

    public void setTrainer(Trainer Trainer) {
        this.Trainer = Trainer;
    }
}
