package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.RegionService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class TrainerCache extends ListenerCache<Trainer> {

    @Inject
    RegionService regionService;

    private String regionId;
    private String areaId;

    @Inject
    public TrainerCache() {
    }

    public void setup(String regionId, String areaId) {
        this.regionId = regionId;
        this.areaId = areaId;
    }

    @Override
    public ICache<Trainer> init() {
        super.init();
        disposables.add(listener.listen(Socket.UDP, String.format("areas.%s.trainers.*.moved", areaId), getDataClass())
                .subscribe(event -> {
                            final Trainer value = event.data();
                            if (!isCacheable(value)) {
                                return;
                            }
                            switch (event.suffix()) {
                                case "created" -> addValue(value);
                                case "updated" -> updateValue(value);
                                case "deleted" -> removeValue(value);
                            }
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
                "region.%s.trainers.*.*", regionId
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
