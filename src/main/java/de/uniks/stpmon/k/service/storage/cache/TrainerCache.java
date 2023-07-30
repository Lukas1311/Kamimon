package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class TrainerCache extends ListenerCache<Trainer, String> {

    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    Provider<TrainerAreaCache> trainerCacheProvider;
    @Inject
    TrainerStorage trainerStorage;

    private TrainerAreaCache areaCache;

    private String regionId;

    @Inject
    public TrainerCache() {
    }

    public TrainerCache setup(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public boolean areSetupValues(String regionId) {
        return this.regionId.equals(regionId);
    }

    @Override
    protected void updateValueFromSocket(Trainer value) {
        Trainer oldTrainer = getValue(value._id()).orElse(null);
        if (oldTrainer != null) {
            // Keep old position because it is not updated by websocket
            value = TrainerBuilder.builder(oldTrainer)
                    .applyWithoutMove(value)
                    .create();
        }
        updateValue(value);
    }

    @Override
    public ICache<Trainer, String> init() {
        super.init();
        disposables.add(regionStorage.onEvents().subscribe(event -> {
            if (areaCache != null) {
                areaCache.destroy();
            }
            if (event.changedArea()) {
                String areaId = event.area()._id();
                // Recreate the trainer cache
                areaCache(areaId);
                if (trainerStorage == null || regionStorage == null) {
                    return;
                }
                Trainer current = trainerStorage.getTrainer();
                Observable<Trainer> trainer = regionService.getTrainer(current.region(), current._id());
                if (trainer == null) {
                    return;
                }
                disposables.add(trainer.subscribe(trainer1 -> {
                    if (trainer1 == null) {
                        return;
                    }
                    trainerStorage.setTrainer(trainer1);
                    updateValueFromSocket(trainer1);
                }));
            }
        }));
        return this;
    }

    @Override
    protected Class<? extends Trainer> getDataClass() {
        return Trainer.class;
    }

    @Override
    protected String getEventName() {
        return String.format(
                "regions.%s.trainers.*.*", regionId
        );
    }

    public String getRegionId() {
        return regionId;
    }

    public TrainerAreaCache areaCache(String areaId) {
        if (areaCache != null && !areaCache.areSetupValues(regionId, areaId)) {
            throw new IllegalStateException("Area cache already setup for different area!");
        }
        if (areaCache == null) {
            areaCache = trainerCacheProvider.get();
            childCaches.add(areaCache);
            areaCache.setup(this, areaId);
            areaCache.addOnDestroy(() -> {
                childCaches.remove(areaCache);
                areaCache = null;
            });
            areaCache.init();
        }
        return areaCache;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (areaCache != null) {
            areaCache.destroy();
        }
    }

    @Override
    protected Observable<List<Trainer>> getInitialValues() {
        return regionService.getTrainers(regionId);
    }

    @Override
    public String getId(Trainer value) {
        return value._id();
    }

}
