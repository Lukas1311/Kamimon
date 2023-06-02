package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerStorage {

    Trainer Trainer;

    private SimpleBooleanProperty trainerNotLoaded = new SimpleBooleanProperty(true);

    @Inject
    public TrainerStorage() {
    }

    public Trainer getTrainer() {
        return Trainer;
    }

    public void setTrainer(Trainer Trainer) {
        this.Trainer = Trainer;
        trainerNotLoaded.set(Trainer == null);
    }

    public SimpleBooleanProperty getTrainerNotLoaded(){
        return trainerNotLoaded;
    }
}
