package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Monster;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;

public class EncounterMember extends SingleCache<Monster> {

    protected EncounterMonsters monsters;
    protected String trainerId;
    protected String monsterId;

    @Inject
    public EncounterMember() {
    }

    public void setup(String trainerId, String monsterId, EncounterMonsters monsters) {
        this.trainerId = trainerId;
        this.monsterId = monsterId;
        this.monsters = monsters;
    }

    public EncounterMember init() {
        // Reset disposable
        destroy();
        disposables = new CompositeDisposable();
        reset();

        if (monsterId == null) {
            reset();
            return this;
        }

        // Listen to changes to the monster
        disposables.add(monsters.listenValue(monsterId)
                .subscribe(event -> {
                            if (event.isPresent()) {
                                setValue(event.get());
                            } else {
                                reset();
                            }
                        }
                ));
        return this;
    }

    public String getTrainerId() {
        return trainerId;
    }
}
