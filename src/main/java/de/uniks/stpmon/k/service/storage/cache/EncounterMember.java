package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;

public class EncounterMember extends SingleCache<Monster> {

    @Inject
    protected EventListener listener;
    @Inject
    protected RegionService regionService;
    @Inject
    protected RegionStorage regionStorage;
    protected String trainerId;
    protected String monsterId;
    protected Completable onInitialized;


    @Inject
    public EncounterMember() {
    }

    public void setup(String trainerId, String monsterId) {
        this.trainerId = trainerId;
        this.monsterId = monsterId;
    }

    public void init() {
        Region region = regionStorage.getRegion();
        if (region == null) {
            throw new IllegalStateException("Region not found");
        }

        // Reset disposable
        if (onInitialized != null) {
            destroy();
            disposables = new CompositeDisposable();
        }

        // Set initial value
        onInitialized = regionService.getMonster(region._id(), trainerId, monsterId)
                .doOnNext(this::setValue).ignoreElements().cache();
        disposables.add(onInitialized.subscribe());
        // Listen to changes to the monster
        disposables.add(listener.listen(Socket.WS,
                        "trainers.%s.monsters.%s.*".formatted(trainerId, monsterId),
                        Monster.class)
                .subscribe(event -> {
                    final Monster value = event.data();
                    switch (event.suffix()) {
                        case "created", "updated" -> setValue(value);
                        case "deleted" -> reset();
                    }
                        }
                ));
    }

    public Completable onInitialized() {
        return onInitialized;
    }

    public String getTrainerId() {
        return trainerId;
    }
}
