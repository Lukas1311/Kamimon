package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class TrainerStorage extends TrainerProvider {

    private final SimpleBooleanProperty trainerLoaded = new SimpleBooleanProperty(false);

    private final BehaviorSubject<Optional<Trainer>> trainerSubject = BehaviorSubject.create();

    @Inject
    public TrainerStorage() {
    }

    public void setTrainer(Trainer trainer) {
        super.setTrainer(trainer);
        trainerLoaded.set(trainer != null);
        trainerSubject.onNext(Optional.ofNullable(trainer));
        if (trainer != null) {
            System.out.printf("x=%d,y=%d%n", trainer.x(), trainer.y());
        }
    }

    public SimpleBooleanProperty getTrainerLoaded() {
        return trainerLoaded;
    }

    public Observable<Optional<Trainer>> onTrainer() {
        return trainerSubject;
    }

    @Override
    public boolean isMain() {
        return true;
    }

}
