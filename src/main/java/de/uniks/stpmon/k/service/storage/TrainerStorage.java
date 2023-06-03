package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerStorage {

    Trainer trainer;

    private final SimpleBooleanProperty trainerLoaded = new SimpleBooleanProperty(false);

    @Inject
    public TrainerStorage() {
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer Trainer) {
        this.trainer = Trainer;
        trainerLoaded.set(Trainer != null);
    }

    public SimpleBooleanProperty getTrainerLoaded(){
        return trainerLoaded;
    }
}
