package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.RegionService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class TrainerCache extends ListenerCache<Trainer> {

    @Inject
    RegionService regionService;

    private String regionId;
    private String areaId;

    @Inject
    public TrainerCache() {
    }

    public TrainerCache setup(String regionId, String areaId) {
        this.regionId = regionId;
        this.areaId = areaId;
        return this;
    }

    public boolean areSetupValues(String regionId, String areaId) {
        return this.regionId.equals(regionId) && this.areaId.equals(areaId);
    }

    @Override
    public ICache<Trainer> init() {
        super.init();
        disposables.add(listener.listen(Socket.UDP, String.format("areas.%s.trainers.*.moved", areaId), MoveTrainerDto.class)
                .subscribe(event -> {
                            final MoveTrainerDto dto = event.data();
                            Optional<Trainer> trainerOptional = getValue(dto._id());
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
                            if (!isCacheable(newTrainer)) {
                                removeValue(trainer);
                                return;
                            }
                            updateValue(newTrainer);
                        }
                ));
        return this;
    }

    @Override
    protected boolean isCacheable(Trainer value) {
        return areaId.equals(value.area());
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

    @Override
    protected Observable<List<Trainer>> getInitialValues() {
        return regionService.getTrainers(regionId, areaId);
    }

    @Override
    public String getId(Trainer value) {
        return value._id();
    }
}
