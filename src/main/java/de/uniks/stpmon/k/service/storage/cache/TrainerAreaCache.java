package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class TrainerAreaCache extends SimpleCache<Trainer, String> {

    private TrainerCache trainerCache;
    private String regionId;
    private String areaId;
    private PositionCache positionCache;

    @Inject
    TrainerStorage trainerStorage;
    @Inject
    EventListener listener;

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
        positionCache = new PositionCache(this);
        childCaches.add(positionCache);

        super.init();
        positionCache.init();
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
                            Trainer newTrainer = TrainerBuilder.builder(trainer).applyMove(dto).create();
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

    private static int getPositionIndex(int x, int y) {
        return x << 0xF | y;
    }

    public Optional<Trainer> getTrainerAt(int x, int y) {
        if (x < 0 || y < 0) {
            return Optional.empty();
        }
        return positionCache.getValue(getPositionIndex(x, y));
    }

    private static class PositionCache extends SimpleCache<Trainer, Integer> {

        private final TrainerAreaCache trainerAreaCache;

        public PositionCache(TrainerAreaCache trainerAreaCache) {
            this.trainerAreaCache = trainerAreaCache;
        }

        @Override
        protected Observable<List<Trainer>> getInitialValues() {
            return trainerAreaCache.onInitialized()
                    .andThen(trainerAreaCache.getValues()
                            // just take the first values
                            .take(1));
        }

        @Override
        @SuppressWarnings("unused")
        public void beforeAdd(Trainer value) {
            Optional<Trainer> oldTrainer = trainerAreaCache.getValue(value._id());
            // Remove old trainer if he exists
            oldTrainer.ifPresent(this::removeValue);
        }

        @Override
        @SuppressWarnings("unused")
        public void beforeUpdate(Trainer value) {
            Optional<Trainer> oldTrainer = trainerAreaCache.getValue(value._id());
            // Remove old trainer if he exists
            oldTrainer.ifPresent(this::removeValue);
        }

        @Override
        public Integer getId(Trainer value) {
            return getPositionIndex(value.x(), value.y());
        }

    }

}
