package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainerStorage extends TrainerProvider {

    private final SimpleBooleanProperty trainerLoaded = new SimpleBooleanProperty(false);

    @Inject
    public TrainerStorage() {
    }

    public void setTrainer(Trainer trainer) {
        super.setTrainer(trainer);
        trainerLoaded.set(trainer != null);
    }

    public SimpleBooleanProperty getTrainerLoaded() {
        return trainerLoaded;
    }

    @Override
    public boolean isMain() {
        return true;
    }

}
