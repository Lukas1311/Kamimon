package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Optional;

public class TrainerProvider extends SingleCache<Trainer> {

    @Inject
    public TrainerProvider() {
    }

    public Trainer getTrainer() {
        return asNullable();
    }

    public void setTrainer(Trainer trainer) {
        setValue(trainer);
    }

    public Observable<Optional<Trainer>> onTrainer() {
        return onValue();
    }

    public boolean isMain() {
        return false;
    }

}
