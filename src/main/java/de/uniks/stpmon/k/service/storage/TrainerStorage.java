package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerStorage {

    Trainer trainer;

    private SimpleBooleanProperty trainerNotLoaded = new SimpleBooleanProperty(true);

    @Inject
    public TrainerStorage() {
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer Trainer) {
        this.trainer = Trainer;
        trainerNotLoaded.set(Trainer == null);
    }

    public SimpleBooleanProperty getTrainerNotLoaded(){
        return trainerNotLoaded;
    }
}
