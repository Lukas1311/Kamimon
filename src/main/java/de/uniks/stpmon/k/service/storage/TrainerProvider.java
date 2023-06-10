package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;

import javax.inject.Inject;

public class TrainerProvider {
    protected Trainer trainer;

    @Inject
    public TrainerProvider() {
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public boolean isMain() {
        return false;
    }
}
