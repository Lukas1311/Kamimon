package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class TrainerAreaCache extends SimpleCache<Trainer, String> {

    private TrainerCache trainerCache;
    @Inject
    TrainerStorage trainerStorage;
    @Inject
    protected EventListener listener;

    private String regionId;
    private String areaId;

    @Inject
    public TrainerAreaCache() {
    }

    public void setup(TrainerCache trainerCache, String areaId) {
        this.trainerCache = trainerCache;
        this.regionId = trainerCache.getRegionId();
        this.areaId = areaId;
    }

    public boolean areSetupValues(String regionId, String areaId) {
        return this.regionId.equals(regionId) && this.areaId.equals(areaId);
    }

    @Override
    public ICache<Trainer, String> init() {
        super.init();
        disposables.add(listener.listen(Socket.UDP,
                        String.format("areas.%s.trainers.*.moved", areaId), MoveTrainerDto.class)
                .subscribe(event -> {
                            final MoveTrainerDto dto = event.data();
                            // Get trainer from parent cache to get trainers which changed area
                            Optional<Trainer> trainerOptional = trainerCache.getValue(dto._id());
                            // Should never happen, trainer moves before he exists
                            if (trainerOptional.isEmpty()) {
                                return;
                            }
                            Trainer trainer = trainerOptional.get();
                            Trainer newTrainer = new Trainer(trainer._id(),
                                    trainer.region(),
                                    trainer.user(),
                                    trainer.name(),
                                    trainer.image(),
                                    trainer.coins(),
                                    dto.area(),
                                    dto.x(),
                                    dto.y(),
                                    dto.direction(),
                                    trainer.npc());
                            trainerCache.updateValue(newTrainer);
                        }
                ));
        return this;
    }

    @Override
    protected boolean isCacheable(Trainer value) {
        Trainer trainer = trainerStorage.getTrainer();
        return areaId.equals(value.area())
                || trainer != null && trainer._id().equals(value._id());
    }

    @Override
    protected Observable<List<Trainer>> getInitialValues() {
        return trainerCache.onInitialized()
                .andThen(trainerCache.getValues()
                        // just take the first values
                        .take(1));
    }

    @Override
    public String getId(Trainer value) {
        return value._id();
    }
}
